package security.simulation;

import security.algorithm.AES;
import security.components.BaggageScanner;
import security.components.Belt;
import security.components.Button;
import security.components.ManualPostControl;
import security.components.OperatingStation;
import security.components.RollerConveyor;
import security.components.Scanner;
import security.components.Supervision;
import security.components.Track;
import security.components.TraySupplyment;
import security.customer.HandBaggage;
import security.customer.Layer;
import security.customer.Passenger;
import security.data.IDCard;
import security.data.enums.ButtonIcon;
import security.data.enums.TypeOfIDCard;
import security.devices.CardReader;
import security.devices.ExplosivesTraceDetector;
import security.staff.Employee;
import security.staff.FederalPoliceOffice;
import security.staff.FederalPoliceOfficer;
import security.staff.Inspector;
import security.staff.Supervisor;
import security.staff.Technician;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

public class Simulation {
    private final BaggageScanner baggageScanner;
    private final List<Passenger> passengers;
    private final List<Employee> employees;
    private final FederalPoliceOffice policeOffice;

    private Simulation (BaggageScanner baggageScanner, List<Passenger> passengers, List<Employee> employees, FederalPoliceOffice policeOffice) {
        this.baggageScanner = baggageScanner;
        this.passengers = passengers;
        this.employees = employees;
        this.policeOffice = policeOffice;
    }


    public static void main (String[] args) {
        Builder simulationBuilder = new Builder();
        simulationBuilder.defaultEmployees();
        try {
            simulationBuilder.defaultPassengers();
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
        final Simulation build = simulationBuilder.build();
        build.initializeSimulation();
    }

    public void initializeSimulation () {
        // Place Employees
        baggageScanner.getRollerConveyor().setWorkingInspector(employees.stream().filter(employee -> employee.getId().equals("I1")).findFirst().orElseThrow());
        baggageScanner.getOperatingStation().setPresentUser(employees.stream().filter(employee -> employee.getId().equals("I2")).findFirst().orElseThrow());
        baggageScanner.getManualPostControl().setWorkingInspector(employees.stream().filter(employee -> employee.getId().equals("I3")).findFirst().orElseThrow());
        baggageScanner.getSupervision().setSupervisor(employees.stream().filter(employee -> employee instanceof Supervisor).findFirst().orElseThrow());
        baggageScanner.setCurrentFederalPoliceOfficer(employees.stream().filter(employee -> employee instanceof FederalPoliceOfficer).findFirst().orElseThrow());

        // Add Passengers
        baggageScanner.getTraySupplyment().getPassengerQueue().addAll(passengers);
    }

    public void runSimulation () {

    }

    public static class Builder {
        private final List<Passenger> passengers = new LinkedList<>();
        private final List<Employee> employees = new ArrayList<>();
        private final Configuration config = new Configuration();

        public void addEmployee (Employee employee) {
            employees.add(employee);
        }

        public void addPassenger (Passenger passenger) {
            passengers.add(passenger);
        }

        public void defaultEmployees () {
            addEmployee(new Inspector("I1", "Clint Eastwood", "31.05.1930", true));
            addEmployee(new Inspector("I2", "Natalie Portman", "09.06.1981", false));
            addEmployee(new Inspector("I3", "Bruce Willis", "19.03.1955", true));
            addEmployee(new Supervisor("S", "Jodie Foster", "19.11.1962", false, false));
            addEmployee(new Technician("T", "Jason Statham", "26.07.1967"));
            addEmployee(new FederalPoliceOfficer("O1", "Wesley Snipes", "31.07.1962", "Officer"));
            addEmployee(new FederalPoliceOfficer("O2", "Toto", "01.01.1969", "Officer"));
            addEmployee(new FederalPoliceOfficer("O3", "Harry", "01.01.1969", "Officer"));
        }

        public void defaultPassengers () throws IOException, URISyntaxException {
            File passengersFile = new File(Simulation.class.getResource("passengers.txt").toURI());
            java.util.Scanner passengerInputScanner = new java.util.Scanner(passengersFile);

            while (passengerInputScanner.hasNextLine()) {
                String[] passengerInformation = passengerInputScanner.nextLine().split(";");
                Passenger passenger = new Passenger(passengerInformation[0]);

                List<HandBaggage> passengerBaggage = new ArrayList<>(passengerInformation.length - 1);
                for (int i = 1 ; i < passengerInformation.length ; i++) {
                    File baggageFile = new File(Simulation.class.getResource("baggage_" + passengerInformation[i] + ".txt").toURI());
                    java.util.Scanner baggageInputScanner = new java.util.Scanner(baggageFile);
                    Layer[] layers = new Layer[5];
                    for (int layerIndex = 0 ; layerIndex < layers.length ; layerIndex++) {
                        layers[layerIndex] = new Layer(baggageInputScanner.nextLine());
                    }
                    passengerBaggage.add(new HandBaggage(passenger, layers));
                }

                passenger.setBaggage(passengerBaggage.toArray(new HandBaggage[0]));
                addPassenger(passenger);
            }
        }

        public Simulation build () {
            // Structures to fill
            FederalPoliceOffice policeOffice = new FederalPoliceOffice();

            // Initialize the Employees.
            // Give everybody an IDCard
            employees.forEach(employee -> {
                String pin = employee.getBirthDate().split("\\.")[2];
                TypeOfIDCard cardType;
                String profiletype;

                switch (employee.getClass().getSimpleName()) {
                    case "Inspector" -> {
                        cardType = TypeOfIDCard.STAFF;
                        profiletype = "I";
                    }
                    case "Supervisor" -> {
                        cardType = TypeOfIDCard.STAFF;
                        profiletype = "S";
                    }
                    case "FederalPoliceOfficer" -> {
                        cardType = TypeOfIDCard.EXTERNAL;
                        profiletype = "O";
                    }
                    case "Technician" -> {
                        cardType = TypeOfIDCard.EXTERNAL;
                        profiletype = "T";
                    }
                    default -> {
                        // Unknown Employeetype
                        cardType = TypeOfIDCard.EXTERNAL;
                        profiletype = "K";
                    }
                }

                String stripe = "***" + profiletype + "***" + pin + "***";
                AES encrypter = new AES(config.getAesKey());
                stripe = encrypter.encrypt(stripe);

                IDCard idCard = new IDCard(employee.getId().hashCode(), Instant.ofEpochMilli(1672527599000L), stripe, false, cardType);
                employee.setIdCard(idCard);
            });

            // Register Officers
            employees.stream()
                     .filter(employee -> employee instanceof FederalPoliceOfficer)
                     .map(employee -> (FederalPoliceOfficer) employee)
                     .forEach(officer -> {
                         policeOffice.getRegisteredOfficers().add(officer);
                         officer.setOffice(policeOffice);
                     });
            policeOffice.getRegisteredOfficers().sort(Comparator.comparing(Employee::getId));

            // Build the BaggageScanner
            BaggageScanner baggageScanner = new BaggageScanner(config.getPermissions());

            // Prepare where preparation is necessary
            Track[] outgoing = new Track[] {new Track(1), new Track(2)};

            OperatingStation operatingStation = new OperatingStation(baggageScanner);

            CardReader reader = new CardReader(new AES(config.getAesKey()), operatingStation);

            Button[] buttons = new Button[3];
            buttons[0] = new Button(ButtonIcon.LEFT_ARROW, operatingStation);
            buttons[1] = new Button(ButtonIcon.RECTANGLE, operatingStation);
            buttons[2] = new Button(ButtonIcon.RIGHT_ARROW, operatingStation);

            operatingStation.setCardReader(reader);
            operatingStation.setButtons(buttons);

            // Assemble the pieces
            baggageScanner.setTraySupplyment(new TraySupplyment(baggageScanner));
            baggageScanner.setRollerConveyor(new RollerConveyor(baggageScanner));
            baggageScanner.setBelt(new Belt());
            baggageScanner.setScanner(new Scanner(config.getSearchAlgorithm()));
            baggageScanner.setOutgoingTracks(outgoing);
            baggageScanner.setManualPostControl(new ManualPostControl(outgoing[1], baggageScanner, new ExplosivesTraceDetector()));
            baggageScanner.setSupervision(new Supervision(baggageScanner));
            baggageScanner.setOperatingStation(operatingStation);

            return new Simulation(baggageScanner, passengers, employees, policeOffice);
        }
    }
}
