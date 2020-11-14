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

    public void takeWeapon (String weapon) {
        System.out.println("Officer " + name + " took " + weapon);
    }

    public void arrestPassenger (Passenger toArrest) {

    }

    public void steerRobot (ExplosiveDisarmRobot robot, BaggageScanner alertedScanner) {

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
