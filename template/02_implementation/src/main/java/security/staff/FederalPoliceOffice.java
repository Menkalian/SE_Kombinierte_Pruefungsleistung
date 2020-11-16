package security.staff;

import security.customer.Passenger;
import security.devices.ExplosiveDisarmRobot;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class FederalPoliceOffice {
    private final List<FederalPoliceOfficer> registeredOfficers;
    // This is no physical representation of passengers. This is just their personal data saved.
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
        System.out.println("Reinforcement requested. Deploying Toto and Harry.");
        return registeredOfficers.subList(1, 3).toArray(new FederalPoliceOfficer[2]);
    }

    public List<FederalPoliceOfficer> getRegisteredOfficers () {
        return registeredOfficers;
    }

    public ExplosiveDisarmRobot supplyDisarmRobot () {
        System.out.println("An ExplosiveDisarmRobot was deployed from the FederalPoliceOffice.");
        Random rng = new Random();
        return robots[rng.nextInt(robots.length)];
    }

    public void takeArrestedPassenger (Passenger arrested) {
        if (!arrested.isArrested()) {
            System.out.println("Passenger " + arrested.getName() + " was arrested and registered at the FederalPoliceOffice.");
            arrestedPassengers.add(arrested);
            arrested.setArrested(true);
            System.out.println("Now Arrested Passengers: ");
            arrestedPassengers.stream().map(Passenger::getName).forEach(System.out::println);
            System.out.println();
        }
    }
}
