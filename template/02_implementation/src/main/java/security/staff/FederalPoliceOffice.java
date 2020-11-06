package security.staff;

import security.customer.Passenger;
import security.devices.ExplosiveDisarmRobot;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class FederalPoliceOffice {
    private ExplosiveDisarmRobot[] robots;
    private List<FederalPoliceOfficer> registeredOfficers;
    private List<Passenger> arrestedPassengers;

    public FederalPoliceOffice () {
        robots = new ExplosiveDisarmRobot[3];
        for (int i = 0 ; i < robots.length ; i++) {
            robots[i] = new ExplosiveDisarmRobot();
        }

        registeredOfficers = new ArrayList<>();
        arrestedPassengers = new LinkedList<>();
    }

    public FederalPoliceOfficer[] requestReinforcment () {
        return null;
    }

    public void takeArrestedPassenger (Passenger arrested) {
        System.out.println("Passenger " + arrested.getName() + " was arrested in the FederalPoliceOffice.");
        arrestedPassengers.add(arrested);
    }
}
