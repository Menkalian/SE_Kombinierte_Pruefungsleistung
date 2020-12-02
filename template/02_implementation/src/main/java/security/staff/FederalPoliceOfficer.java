package security.staff;

import security.components.BaggageScanner;
import security.components.ManualPostControl;
import security.components.Tray;
import security.customer.HandBaggage;
import security.customer.Passenger;
import security.data.ScanResult;
import security.devices.ExplosiveDisarmRobot;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class FederalPoliceOfficer extends Employee {
    private final String grade;
    private FederalPoliceOffice office;


    public FederalPoliceOfficer (String id, String name, String birthDate, String grade) {
        this.id = id;
        this.name = name;
        this.birthDate = birthDate;
        this.grade = grade;
    }


    public void arrestPassenger (Passenger toArrest) {
        if (!toArrest.isArrested()) {
            office.takeArrestedPassenger(toArrest);
            System.out.printf("Pol. Officer: Passenger \"%s\" was arrested by \"%s\".%n", toArrest.getName(), name);
        }
    }

    public void takeWeapon (String weapon) {
        System.out.printf("Pol. Officer: \"%s\" took weapon: \"%s\"%n", name, weapon);
    }

    public void steerRobot (ExplosiveDisarmRobot robot, BaggageScanner alertedScanner) {
        System.out.printf("Pol. Officer: \"%s\" is steering the robot to destroy the baggage%n", name);
        robot.destroyBaggage(alertedScanner.getManualPostControl().getCurrentTrayToInvestigate().takeBaggage());
    }

    public void notifyWeapon (BaggageScanner scanner) {
        System.out.println("Pol. Officer: \"" + getName() + "\" was notified there is a weapon in the baggage");

        final ManualPostControl postControl = scanner.getManualPostControl();
        postControl.setCurrentTrayToInvestigate(postControl.getBelongingTrack().getTrays().removeLast());
        final HandBaggage handBaggage = postControl.getCurrentTrayToInvestigate().takeBaggage();

        // Additionally call supervisor for removing a weapon. Passenger was already called when alert was activated
        Supervisor presentSupervisor = (Supervisor) scanner.getSupervision().getSupervisor();

        final List<HandBaggage> toRemove = Arrays.stream(scanner.getManualPostControl().getPresentPassenger().getBaggage()).collect(Collectors.toList());
        toRemove.remove(handBaggage);

        ScanResult lastResult = scanner.getScanResults().get(scanner.getScanResults().size() - 1).getResult();
        final String taken = handBaggage.takeContent(lastResult.getPosition()[0], lastResult.getPosition()[1], lastResult.getItemType().getSignature().length());

        System.out.printf(
                "Pol. Officer: \"%s\" took '%s' out of the Baggage and is now handing it to Officer 3. The Baggage was opened whilst \"%s\" and Supervisor \"%s\" were present.%n",
                getName(),
                taken,
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

        System.out.println("Pol. Officer: All Baggage of the passenger was checked. It will now be taken away with them");
        // Remove remaining Baggage from Scanner
        for (HandBaggage baggage : toRemove) {
            Tray removalTray = new Tray(baggage);

            // If the component does not contain the tray the call of remove is ignored
            // There might be a special case here where some baggage after the scanned one was destroyed/removed and is not available anymore.
            // In this case all three calls below do not change any Collections
            scanner.getBelt().getTrayQueue().remove(removalTray);
            scanner.getOutgoingTracks()[0].getTrays().remove(removalTray);
            scanner.getOutgoingTracks()[1].getTrays().remove(removalTray);
        }

    }


    public String getGrade () {
        return grade;
    }


    public FederalPoliceOffice getOffice () {
        return office;
    }

    public void setOffice (FederalPoliceOffice office) {
        this.office = office;
    }
}
