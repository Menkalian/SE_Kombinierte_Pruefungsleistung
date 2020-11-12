package security.staff;

import security.customer.Passenger;
import security.devices.ExplosiveDisarmRobot;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class FederalPoliceOffice {
    private final List<FederalPoliceOfficer> registeredOfficers;
    private final List<Passenger> arrestedPassengers;
    private final ExplosiveDisarmRobot[] robots;

    public FederalPoliceOffice () {
        robots = new ExplosiveDisarmRobot[3];
        for (int i = 0 ; i < robots.length ; i++) {
            robots[i] = new ExplosiveDisarmRobot();
        }

        registeredOfficers = new ArrayList<>();
        arrestedPassengers = new LinkedList<>();
    }

    public FederalPoliceOfficer[] requestReinforcment () {
        System.out.println("Reinforcement ist being deployed from FederalPoliceOffice");
        return registeredOfficers.subList(1, 3).toArray(new FederalPoliceOfficer[2]);
    }

    public List<FederalPoliceOfficer> getRegisteredOfficers () {
        return registeredOfficers;
    }

    public ExplosiveDisarmRobot supplyDisarmRobot () {
        System.out.println("Supplying ExplosivesDisarmRobot");
        Random rng = new Random();
        return robots[rng.nextInt(robots.length)];
    }

    public void takeArrestedPassenger (Passenger arrested) {
        System.out.println("Passenger " + arrested.getName() + " was arrested in the FederalPoliceOffice.");
        arrestedPassengers.add(arrested);
        System.out.println("Now Arrested Passengers: ");
        arrestedPassengers.stream().map(Passenger::getName).forEach(System.out::println);
        System.out.println();
    }
}
