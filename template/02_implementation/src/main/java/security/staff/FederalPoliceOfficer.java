package security.staff;

import security.components.BaggageScanner;
import security.customer.Passenger;
import security.devices.ExplosiveDisarmRobot;

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
