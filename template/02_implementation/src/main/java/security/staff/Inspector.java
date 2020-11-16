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
import security.data.enums.ScanResultType;
import security.devices.ExplosivesTestStrip;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Inspector extends Employee {
    private final boolean isSenior;

    public Inspector (String id, String name, String birthDate, boolean isSenior) {
        this.id = id;
        this.name = name;
        this.birthDate = birthDate;
        this.isSenior = isSenior;
    }

    public void pushTray (BaggageScanner scanner) {
        while (!scanner.getRollerConveyor().getTrayQueue().isEmpty())
            scanner.getRollerConveyor().pushTrays();
    }

    public void pushButton (Button button) {
        System.out.println("Inspector \"" + name + "\" is pushing the Button \"" + button.toString() + "\".");
        button.push();
        // React to Scan-Result
        if (button.getIcon() == ButtonIcon.RECTANGLE) {
            final BaggageScanner baggageScanner = button.getConnectedOperatingStation().getConnectedScanner();
            final List<Record> scanResults = baggageScanner.getScanResults();
            final Record lastResult = scanResults.get(scanResults.size() - 1);
            if (lastResult.getResult().getType() == ScanResultType.CLEAN) {
                System.out.println("Inspector \"" + getName() + "\" identified the scan as clean. Proceeding as usual.");
            } else {
                switch (lastResult.getResult().getItemType()) {
                    case KNIFE -> {
                        System.out.println("Inspector \"" + getName() + "\" recognized a knife. Rerouting baggage to ManualPostControl");
                        final Tray scannedTray = baggageScanner.getScanner().move(null);
                        baggageScanner.getOutgoingTracks()[0].trayArrive(scannedTray);
                        ((Inspector) baggageScanner.getManualPostControl().getWorkingInspector()).notifyKnife(baggageScanner);
                    }
                    case WEAPON -> {
                        System.out.println("Inspector \"" + getName() + "\" recognized a weapon. Rerouting baggage to ManualPostControl");
                        triggerAlert(baggageScanner);

                        // People leaving.
                        baggageScanner.getManualPostControl().setPresentOfficers(null);
                        baggageScanner.getManualPostControl().setPresentPassenger(null);

                        // Continue scanning
                        ((Supervisor) baggageScanner.getSupervision().getSupervisor()).unlockScanner(baggageScanner);
                        baggageScanner.getOperatingStation().getPresentUser().enterPIN(baggageScanner.getOperatingStation().getCardReader());
                    }
                    case EXPLOSIVE -> {
                        System.out.println("Inspector \"" + getName() + "\" recognized an explosive. Rerouting baggage to ManualPostControl");
                        triggerAlert(baggageScanner);
                        final Tray scannedTray = baggageScanner.getScanner().move(null);
                        baggageScanner.getOutgoingTracks()[0].trayArrive(scannedTray);
                        baggageScanner.getManualPostControl().setCurrentTrayToInvestigate(baggageScanner.getOutgoingTracks()[0].getTrays().remove(0));

                        // Save the persons baggage to remove it from the scanner later
                        final List<HandBaggage> toRemove = Arrays.stream(baggageScanner.getManualPostControl().getPresentPassenger().getBaggage()).collect(Collectors.toList());
                        toRemove.remove(scannedTray.getContainedBaggage());
                        ((Inspector) baggageScanner.getManualPostControl().getWorkingInspector()).testBaggageForExplosives(baggageScanner.getManualPostControl());

                        // Remove Baggage from Scanner
                        for (HandBaggage handBaggage : toRemove) {
                            Tray trayToRemove = baggageScanner.getBelt().getTrayQueue().stream().filter(tray -> tray.getContainedBaggage().equals(handBaggage)).findFirst().orElse(null);
                            if (trayToRemove != null) {
                                baggageScanner.getBelt().getTrayQueue().remove(trayToRemove);
                                continue;
                            }
                            trayToRemove = baggageScanner.getOutgoingTracks()[1].getTrays().stream().filter(tray -> tray.getContainedBaggage().equals(handBaggage)).findFirst().orElseThrow();
                            baggageScanner.getOutgoingTracks()[1].getTrays().remove(trayToRemove);
                        }

                        // People leaving.
                        baggageScanner.getManualPostControl().setPresentOfficers(null);
                        baggageScanner.getManualPostControl().setPresentPassenger(null);

                        // Continue scanning
                        ((Supervisor) baggageScanner.getSupervision().getSupervisor()).unlockScanner(baggageScanner);
                        baggageScanner.getOperatingStation().getPresentUser().enterPIN(baggageScanner.getOperatingStation().getCardReader());
                    }
                }
            }
        }
    }

    public void notifyKnife (BaggageScanner scanner) {
        System.out.println("Inspector \"" + getName() + "\" was notified there is a knife in the baggage.");
        // Take Tray from track to Control
        final Track[] tracks = scanner.getOutgoingTracks();
        scanner.getManualPostControl().setCurrentTrayToInvestigate(tracks[0].getTrays().remove(0));
        final Passenger passenger = scanner.getManualPostControl().getCurrentTrayToInvestigate().getContainedBaggage().getOwner();
        tracks[1].callPassenger(passenger);
        scanner.getManualPostControl().setPresentPassenger(passenger);

        final ScanResult result = scanner.getScanResults().get(scanner.getScanResults().size() - 1).getResult();
        final String takenContent = scanner.getManualPostControl().getCurrentTrayToInvestigate().getContainedBaggage()
                                           .takeContent(result.getPosition()[0], result.getPosition()[1], result.getItemType().getSignature().length());
        System.out.println("Inspector \"" + getName() + "\" has taken '" + takenContent + "' and got rid of it.");

        // Put tray back and let passanger get back.
        scanner.getManualPostControl().setPresentPassenger(null);
        scanner.getOutgoingTracks()[1].passengerWaiting(passenger);

        tracks[0].getTrays().add(scanner.getManualPostControl().getCurrentTrayToInvestigate());
        scanner.getManualPostControl().setCurrentTrayToInvestigate(null);
        ((Inspector) scanner.getOperatingStation().getPresentUser()).pushButton(scanner.getOperatingStation().getButtons()[0]);
        ((Inspector) scanner.getOperatingStation().getPresentUser()).pushButton(scanner.getOperatingStation().getButtons()[1]);
    }

    public void triggerAlert (IBaggageScanner scanner) {
        System.out.println("Inspector \"" + getName() + "\" is activating the alert.");
        scanner.alert();
    }

    public void testBaggageForExplosives (ManualPostControl postControl) {
        System.out.println("\"" + getName() + "\" is testing a baggage for Explosives. Stay back!");
        final ExplosivesTestStrip stripe = postControl.getCurrentTrayToInvestigate().getContainedBaggage().swipeTest();
        final boolean result = postControl.getExplosivesTraceDetector().testStripe(stripe);

        if (result) {
            System.out.println("Explosive confirmed! Destroying Baggage!");
            final FederalPoliceOfficer explosiveDestructionOfficer = Arrays
                    .stream(postControl.getPresentOfficers())
                    .filter(officer -> officer.getId().equals("O2"))
                    .findFirst().orElseThrow();
            explosiveDestructionOfficer.steerRobot(explosiveDestructionOfficer.getOffice().supplyDisarmRobot(), postControl.getConnectedScanner());
        } else {
            // Should not happen in this simulation
            System.out.println("The Explosive could not be confirmed.");
        }
    }

    public boolean isSenior () {
        return isSenior;
    }
}
