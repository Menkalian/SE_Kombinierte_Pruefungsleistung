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
                        final Tray scannedTray = baggageScanner.getScanner().move(null);
                        baggageScanner.getOutgoingTracks()[0].trayArrive(scannedTray);

                        ((Inspector) baggageScanner.getManualPostControl().getWorkingInspector()).notifyWeapon(baggageScanner);

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
                        baggageScanner.getManualPostControl().setCurrentTrayToInvestigate(baggageScanner.getOutgoingTracks()[0].getTrays().remove(baggageScanner.getOutgoingTracks()[0].getTrays().size() - 1));

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
                            trayToRemove = baggageScanner.getOutgoingTracks()[0].getTrays().stream().filter(tray -> tray.getContainedBaggage().equals(handBaggage)).findFirst().orElse(null);
                            if (trayToRemove != null) {
                                baggageScanner.getOutgoingTracks()[0].getTrays().remove(trayToRemove);
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
        scanner.getManualPostControl().setCurrentTrayToInvestigate(tracks[0].getTrays().remove(tracks[0].getTrays().size() - 1));
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

    private void notifyWeapon (BaggageScanner scanner) {
        System.out.println("Inspector \"" + getName() + "\" was notified there is a weapon in the baggage.");
        final ManualPostControl postControl = scanner.getManualPostControl();
        postControl.setCurrentTrayToInvestigate(postControl.getBelongingTrack().getTrays().remove(postControl.getBelongingTrack().getTrays().size() - 1));
        Supervisor presentSupervisor = (Supervisor) scanner.getSupervision().getSupervisor();
        final HandBaggage handBaggage = postControl.getCurrentTrayToInvestigate().takeBaggage();
        final List<HandBaggage> toRemove = Arrays.stream(scanner.getManualPostControl().getPresentPassenger().getBaggage()).collect(Collectors.toList());
        toRemove.remove(handBaggage);
        ScanResult lastResult = scanner.getScanResults().get(scanner.getScanResults().size() - 1).getResult();
        final String taken = handBaggage.takeContent(lastResult.getPosition()[0], lastResult.getPosition()[1], lastResult.getItemType().getSignature().length());
        System.out.printf(
                "Inspector \"%s\" took '%s' out of the Baggage and is now handing it to Officer 3. The Baggage was opened whilst \"%s\" and Supervisor \"%s\" were present.%n",
                getName(), taken,
                postControl.getPresentPassenger().getName(),
                presentSupervisor.getName()
        );
        postControl.getPresentOfficers()[2].takeWeapon(taken);
        postControl.getCurrentTrayToInvestigate().putBaggage(handBaggage);
        postControl.getBelongingTrack().getTrays().add(postControl.getCurrentTrayToInvestigate());
        postControl.setCurrentTrayToInvestigate(null);

        // Unlock scanner
        ((Supervisor) scanner.getSupervision().getSupervisor()).unlockScanner(scanner);
        scanner.getOperatingStation().getPresentUser().enterPIN(scanner.getOperatingStation().getCardReader());

        // Check further baggage of the passenger.
        while (!scanner.getBelt().getTrayQueue().isEmpty()) {
            ((Inspector) scanner.getOperatingStation().getPresentUser()).pushButton(scanner.getOperatingStation().getButtons()[2]);
            ((Inspector) scanner.getOperatingStation().getPresentUser()).pushButton(scanner.getOperatingStation().getButtons()[1]);
        }
        ((Inspector) scanner.getOperatingStation().getPresentUser()).pushButton(scanner.getOperatingStation().getButtons()[2]);

        System.out.println("All Baggage of the passenger was checked. It will now be taken away with them.");
        // Remove Baggage from Scanner
        for (HandBaggage passengerBaggage : toRemove) {
            Tray trayToRemove = scanner.getBelt().getTrayQueue().stream().filter(tray -> tray.getContainedBaggage().equals(passengerBaggage)).findFirst().orElse(null);
            if (trayToRemove != null) {
                scanner.getBelt().getTrayQueue().remove(trayToRemove);
                continue;
            }
            trayToRemove = scanner.getOutgoingTracks()[0].getTrays().stream().filter(tray -> tray.getContainedBaggage().equals(passengerBaggage)).findFirst().orElse(null);
            if (trayToRemove != null) {
                scanner.getOutgoingTracks()[0].getTrays().remove(trayToRemove);
                continue;
            }
            trayToRemove = scanner.getOutgoingTracks()[1].getTrays().stream().filter(tray -> tray.getContainedBaggage().equals(passengerBaggage)).findFirst().orElseThrow();
            scanner.getOutgoingTracks()[1].getTrays().remove(trayToRemove);
        }

    }
}
