package security.staff;

import security.components.BaggageScanner;
import security.customer.Passenger;
import security.devices.ExplosiveDisarmRobot;

public class FederalPoliceOfficer extends Employee {
    private String grade;
    private FederalPoliceOffice office;

    public FederalPoliceOfficer (String id, String name, String birthDate, String grade, FederalPoliceOffice office) {
        this.id = id;
        this.name = name;
        this.birthDate = birthDate;
        this.grade = grade;
        this.office = office;
    }

    public void takeWeapon (String weapon) {

    }

    public void arrestPassenger(Passenger toArrest){

    }

    public void steerRobot(ExplosiveDisarmRobot robot, BaggageScanner alertedScanner){

    }
}
