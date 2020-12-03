package security.staff;

import security.components.BaggageScanner;
import security.components.Button;
import security.components.IBaggageScanner;
import security.components.ManualPostControl;
import security.components.Track;
import security.components.Tray;
import security.customer.HandBaggage;
import security.customer.Passenger;
import security.data.Record;
import security.data.ScanResult;
import security.data.enums.ButtonIcon;
import security.data.enums.ProhibitedItem;
import security.data.enums.ScanResultType;
import security.devices.ExplosivesTestStrip;
import security.state.Locked;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Inspector extends Employee {
    private final boolean isSenior;


    public Inspector (String id, String name, String birthDate, boolean isSenior) {
        this.id = id;
        this.name = name;
        this.birthDate = birthDate;
        this.isSenior = isSenior;
    }


    public void pushTray (BaggageScanner scanner) {
        while (!scanner.getRollerConveyor().getTrayQueue().isEmpty()) {
            System.out.printf("Inspector   : \"%s\" is pushing a Tray%n", getName());
            scanner.getRollerConveyor().pushTrays();
        }
    }

    public void pushButton (Button button) {
        System.out.printf("Inspector   : \"%s\" is pushing the Button \"%s\".%n", getName(), button.toString());
        button.push();

        // React to Scan-Result
        if (button.getIcon() == ButtonIcon.RECTANGLE) {
            final BaggageScanner baggageScanner = button.getConnectedOperatingStation().getConnectedScanner();
            final Record lastResult = baggageScanner.getScanResults().getLast();

            if (lastResult.getResult().getType() == ScanResultType.CLEAN) {
                System.out.printf("Inspector   : \"%s\" identified the scan as clean. No special action is needed%n", getName());
            } else {
                reactToProhibitedItem(lastResult.getResult().getItemType(), baggageScanner);
            }
        }
    }

    public void reactToProhibitedItem (ProhibitedItem itemType, BaggageScanner baggageScanner) {
        System.out.printf("Inspector   : \"%s\" is reacting to a prohibited item. Rerouting baggage to ManualPostControl and taking appropriate action%n", getName());

        // Let I3 move the Tray
        final Tray scannedTray = ((Inspector) baggageScanner.getManualPostControl().getWorkingInspector()).moveTrayToMPC(baggageScanner);

        switch (itemType) {
            case KNIFE -> {
                System.out.printf("Inspector   : \"%s\" recognized a knife. Rerouting baggage to ManualPostControl%n", getName());

                ((Inspector) baggageScanner.getManualPostControl().getWorkingInspector()).notifyKnife(baggageScanner);
                // Scanner is not locked, so it is not necessary to unlock
                return;
            }
            case WEAPON -> {
                System.out.printf("Inspector   : \"%s\" recognized a weapon. Rerouting baggage to ManualPostControl%n", getName());

                triggerAlert(baggageScanner);
                ((FederalPoliceOfficer) baggageScanner.getCurrentFederalPoliceOfficer()).notifyWeapon(baggageScanner);
            }
            case EXPLOSIVE -> {
                System.out.printf("Inspector   : \"%s\" recognized an explosive. Rerouting baggage to ManualPostControl%n", getName());

                triggerAlert(baggageScanner);

                // Save the persons remaining baggage to remove it from the scanner later
                // Additional constructor needed since Arrays.asList gives an unmodifiable List
                final List<HandBaggage> toRemove = new ArrayList<>(Arrays.asList(baggageScanner.getManualPostControl().getPresentPassenger().getBaggage()));
                toRemove.remove(scannedTray.getContainedBaggage());

                // React to Explosives
                ((Inspector) baggageScanner.getManualPostControl().getWorkingInspector()).testBaggageForExplosives(baggageScanner.getManualPostControl());

                // Remove remaining Baggage from Scanner
                for (HandBaggage handBaggage : toRemove) {
                    Tray removalTray = new Tray(handBaggage);

                    // If the component does not contain the tray the call of remove is ignored
                    baggageScanner.getBelt().getTrayQueue().remove(removalTray);
                    baggageScanner.getOutgoingTracks()[0].getTrays().remove(removalTray);
                    baggageScanner.getOutgoingTracks()[1].getTrays().remove(removalTray);
                }
            }
        }

        // People leaving.
        baggageScanner.getManualPostControl().setPresentOfficers(null);
        baggageScanner.getManualPostControl().setPresentPassenger(null);

        // Continue scanning by unlocking the scanner if necessary
        if (baggageScanner.getCurrentState() instanceof Locked) {
            ((Supervisor) baggageScanner.getSupervision().getSupervisor()).unlockScanner(baggageScanner);
            baggageScanner.getOperatingStation().getPresentUser().enterPIN(baggageScanner.getOperatingStation().getCardReader());
        }
    }

    public void triggerAlert (IBaggageScanner scanner) {
        System.out.printf("Inspector   : \"%s\" is activating the alert.%n", getName());
        scanner.alert();
    }

    public void notifyKnife (BaggageScanner scanner) {
        System.out.printf("Inspector   : \"%s\" was notified there is a knife in the baggage%n", getName());

        // Take Tray from track to postControl
        final ManualPostControl postControl = scanner.getManualPostControl();
        final Track[] tracks = scanner.getOutgoingTracks();
        postControl.setCurrentTrayToInvestigate(tracks[0].getTrays().removeLast());

        // Call Passenger to be present when opening their baggage
        final Passenger passenger = postControl.getCurrentTrayToInvestigate().getContainedBaggage().getOwner();
        tracks[1].callPassenger(passenger);
        postControl.setPresentPassenger(passenger);

        // Take Knife out of the baggage
        final ScanResult result = scanner.getScanResults().getLast().getResult();
        final String takenContent = postControl.getCurrentTrayToInvestigate().getContainedBaggage().takeContent(
                result.getPosition()[0], result.getPosition()[1], result.getItemType().getSignature().length()
        );
        System.out.printf("Inspector   : \"%s\" has taken '%s' and got rid of it.%n", getName(), takenContent);

        // Put tray back and let passenger get back.
        scanner.getOutgoingTracks()[1].passengerWaiting(passenger);
        postControl.setPresentPassenger(null);

        tracks[0].getTrays().add(postControl.getCurrentTrayToInvestigate());
        postControl.setCurrentTrayToInvestigate(null);

        // Move Belt Backwards and retry scan
        ((Inspector) scanner.getOperatingStation().getPresentUser()).pushButton(scanner.getOperatingStation().getButtons()[0]);
        ((Inspector) scanner.getOperatingStation().getPresentUser()).pushButton(scanner.getOperatingStation().getButtons()[1]);
    }

    public void testBaggageForExplosives (ManualPostControl postControl) {
        System.out.printf("Inspector   : \"%s\" is testing a baggage for Explosives. Stay back!%n", getName());
        postControl.setCurrentTrayToInvestigate(postControl.getBelongingTrack().getTrays().removeLast());

        final ExplosivesTestStrip stripe = postControl.getCurrentTrayToInvestigate().getContainedBaggage().swipeTest();
        final boolean result = postControl.getExplosivesTraceDetector().testStripe(stripe);

        if (result) {
            System.out.println("Inspector   : Explosive confirmed! Destroying Baggage!");
            final FederalPoliceOfficer explosiveDestructionOfficer = Arrays
                    .stream(postControl.getPresentOfficers())
                    .filter(officer -> officer.getId().equals("O2"))
                    .findFirst().orElseThrow();
            explosiveDestructionOfficer.steerRobot(explosiveDestructionOfficer.getOffice().supplyDisarmRobot(), postControl.getConnectedScanner());
        } else {
            // Should not happen in this simulation
            System.out.println("Inspector   : The Explosive could not be confirmed.");
        }
    }

    public boolean isSenior () {
        return isSenior;
    }

    private Tray moveTrayToMPC (BaggageScanner baggageScanner) {
        final Tray scannedTray = baggageScanner.getScanner().move(null);
        baggageScanner.getOutgoingTracks()[0].trayArrive(scannedTray);
        return scannedTray;
    }
}
