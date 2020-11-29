package security.staff;

import security.customer.Passenger;
import security.devices.ExplosiveDisarmRobot;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class FederalPoliceOffice {
    private final List<FederalPoliceOfficer> registeredOfficers;
    private final ExplosiveDisarmRobot[] robots;

    // This is no physical representation of passengers. This is just their personal data saved.
    private final List<Passenger> arrestedPassengers;


    public FederalPoliceOffice () {
        robots = new ExplosiveDisarmRobot[3];
        for (int i = 0 ; i < robots.length ; i++) {
            robots[i] = new ExplosiveDisarmRobot();
        }

        registeredOfficers = new ArrayList<>();
        arrestedPassengers = new LinkedList<>();
    }


    public FederalPoliceOfficer[] requestReinforcement () {
        System.out.println("PoliceOffice: Reinforcement requested. Deploying Toto and Harry.");

        return registeredOfficers.subList(1, 3).toArray(new FederalPoliceOfficer[2]);
    }

    public ExplosiveDisarmRobot supplyDisarmRobot () {
        System.out.println("PoliceOffice: An ExplosiveDisarmRobot was deployed from the FederalPoliceOffice.");

        Random rng = new Random();
        return robots[rng.nextInt(robots.length)];
    }

    public void takeArrestedPassenger (Passenger arrested) {
        if (!arrested.isArrested()) {
            arrestedPassengers.add(arrested);
            arrested.setArrested(true);

            System.out.printf("PoliceOffice: Passenger %s was arrested and registered at the FederalPoliceOffice.%n", arrested.getName());
            System.out.printf("PoliceOffice: Now Arrested Passengers: %s%n", arrestedPassengers);
        }
    }


    public List<FederalPoliceOfficer> getRegisteredOfficers () {
        return registeredOfficers;
    }
}
