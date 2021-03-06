import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import security.customer.HandBaggage;
import security.customer.Layer;
import security.customer.Passenger;
import security.data.enums.ProhibitedItem;
import security.simulation.Simulation;
import security.staff.Inspector;
import security.staff.Supervisor;
import security.staff.Technician;
import security.state.Activated;
import security.state.Locked;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SimulationTest {

    private Simulation build;

    @BeforeEach
    public void setUp () {
        try {
            System.setOut(new PrintStream("log.txt"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        security.simulation.Simulation.Builder simulationBuilder = new Simulation.Builder();
        simulationBuilder.defaultEmployees();
        try {
            simulationBuilder.defaultPassengers();
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
        build = simulationBuilder.build();
        build.initializeSimulation();
    }

    @Order(1)
    @Test
    public void test1_passengersAndBaggage () throws FileNotFoundException, URISyntaxException {
        //Passenger Count correct
        Assertions.assertEquals(568, (long) build.getPassengers().size());
        //Baggage Count correct
        int baggageCount = 0;
        for (int i = 0 ; i < build.getPassengers().size() ; i++) {
            for (int j = 0 ; j < build.getPassengers().get(i).getBaggage().length ; j++) {
                baggageCount++;
            }
        }
        Assertions.assertEquals(609, baggageCount);
        //Passengers initialized correctly
        List<Passenger> passengersCorrect = new LinkedList<>();
        List<HandBaggage> handBaggageCorrect = new LinkedList<>();

        File passengersFile = new File(Simulation.class.getResource("passengers.txt").toURI());
        java.util.Scanner passengerInputScanner = new java.util.Scanner(passengersFile);

        while (passengerInputScanner.hasNextLine()) {
            String[] passengerInformation = passengerInputScanner.nextLine().split(";");
            Passenger passenger = new Passenger(passengerInformation[0]);

            List<HandBaggage> passengerBaggage = new ArrayList<>(passengerInformation.length - 1);
            int b = 0;
            for (int i = 1 ; i < passengerInformation.length ; i++) {
                File baggageFile = new File(Simulation.class.getResource("baggage_" + passengerInformation[i] + ".txt").toURI());
                java.util.Scanner baggageInputScanner = new java.util.Scanner(baggageFile);
                Layer[] layers = new Layer[5];
                for (int layerIndex = 0 ; layerIndex < layers.length ; layerIndex++) {
                    layers[layerIndex] = new Layer(baggageInputScanner.nextLine());
                }
                HandBaggage handBaggage = new HandBaggage(passenger, layers);
                passengerBaggage.add(handBaggage);
                handBaggageCorrect.add(handBaggage);
                b++;
            }

            passenger.setBaggage(passengerBaggage.toArray(new HandBaggage[0]));
            passengersCorrect.add(passenger);
        }

        for (int i = 0 ; i < build.getPassengers().size() ; i++) {
            Assertions.assertEquals(passengersCorrect.get(i).getName(), build.getPassengers().get(i).getName());
            Assertions.assertEquals(passengersCorrect.get(i).getBaggage().length, build.getPassengers().get(i).getBaggage().length);
            Assertions.assertEquals(passengersCorrect.get(i).isArrested(), build.getPassengers().get(i).isArrested());
        }

        int b = 0;
        for (int i = 0 ; i < build.getPassengers().size() ; i++) {
            for (int j = 0 ; j < build.getPassengers().get(i).getBaggage().length ; j++) {
                Assertions.assertEquals(handBaggageCorrect.get(b).getOwner().getName(), build.getPassengers().get(i).getBaggage()[j].getOwner().getName());
                Assertions.assertEquals(handBaggageCorrect.get(b).getLayers().length, build.getPassengers().get(i).getBaggage()[j].getLayers().length);
                b++;
            }
        }
    }

    @Order(2)
    @Test
    public void test2_occupiedCorrectly () {
        //I1-Clint Eastwood at RollerConveyor
        Assertions.assertEquals("Clint Eastwood", build.getBaggageScanner().getRollerConveyor().getWorkingInspector().getName());
        //I2-Natalie Portman at OperatingStation
        Assertions.assertEquals("Natalie Portman", build.getBaggageScanner().getOperatingStation().getPresentUser().getName());
        //S-Jodie Foster at Supervision
        Assertions.assertEquals("Jodie Foster", build.getBaggageScanner().getSupervision().getSupervisor().getName());
        //I3-Bruce Willis at ManualPostControl
        Assertions.assertEquals("Bruce Willis", build.getBaggageScanner().getManualPostControl().getWorkingInspector().getName());
    }

    @Order(3)
    @Test
    public void test3_lockIDCard () {
        Assertions.assertFalse(build.getBaggageScanner().getOperatingStation().getPresentUser().getIdCard().isLocked());
        build.getBaggageScanner().getOperatingStation().getCardReader().swipeCard(build.getBaggageScanner().getOperatingStation().getPresentUser().getIdCard());
        build.getBaggageScanner().getOperatingStation().getCardReader().enterPin("");
        Assertions.assertFalse(build.getBaggageScanner().getOperatingStation().getPresentUser().getIdCard().isLocked());
        build.getBaggageScanner().getOperatingStation().getCardReader().enterPin("");
        Assertions.assertFalse(build.getBaggageScanner().getOperatingStation().getPresentUser().getIdCard().isLocked());
        build.getBaggageScanner().getOperatingStation().getCardReader().enterPin("");
        Assertions.assertTrue(build.getBaggageScanner().getOperatingStation().getPresentUser().getIdCard().isLocked());
    }

    @Order(4)
    @Test
    public void test4_koBaggageScanner () {
        Assertions.assertThrows(RuntimeException.class, () -> build.getBaggageScanner().getOperatingStation().getCardReader().swipeCard(build.getEmployees().get("O1").getIdCard()));
        Assertions.assertThrows(RuntimeException.class, () -> build.getBaggageScanner().getOperatingStation().getCardReader().swipeCard(build.getEmployees().get("K").getIdCard()));
    }

    @Order(5)
    @Test
    public void test5_onlyProfileFunctions () {
        Assertions.assertTrue(true);
        // Turn Scanner on
        ((Supervisor) build.getBaggageScanner().getSupervision().getSupervisor()).switchPower(build.getBaggageScanner());

        //I
        build.getBaggageScanner().getOperatingStation().setAuthenticatedUserType("I");
        build.getBaggageScanner().setCurrentState(new Activated());

        //MoveBeltForward - Rights sufficient
        Assertions.assertDoesNotThrow(() -> {
            try {
                build.getBaggageScanner().moveBeltForward();
            } catch (NoSuchElementException | NullPointerException ex) {
                //Expected Exceptions
            }
        });

        //MoveBeltBackwards - Rights sufficient
        Assertions.assertDoesNotThrow(() -> {
            try {
                build.getBaggageScanner().moveBeltBackwards();
            } catch (NoSuchElementException | NullPointerException ex) {
                //These two exceptions are expected for this test.
            }
        });

        //Scan - Rights sufficient
        Assertions.assertDoesNotThrow(() -> {
            try {
                build.getBaggageScanner().scan();
            } catch (NullPointerException ex) {
//expected Exception
            }
        });

        //Alarm - Rights sufficient
        build.getBaggageScanner().setCurrentState(new Activated());
        Assertions.assertDoesNotThrow(() -> {
            try {
                build.getBaggageScanner().alert();
            } catch (NoSuchElementException ex) {
//expected Exception
            }
        });

        //Report - Rights insufficient
        Assertions.assertThrows(RuntimeException.class, () -> build.getBaggageScanner().report());

        //Maintenance - Rights insufficient
        Assertions.assertThrows(RuntimeException.class, () -> build.getBaggageScanner().maintenance());

        //S
        build.getBaggageScanner().getOperatingStation().setAuthenticatedUserType("S");
        build.getBaggageScanner().setCurrentState(new Activated());

        //MoveBeltForward - Rights insufficient
        Assertions.assertThrows(RuntimeException.class, () -> {
            try {
                build.getBaggageScanner().moveBeltForward();
            } catch (NoSuchElementException | NullPointerException ex) {
                //Expected Exceptions
            }
        });

        //MoveBeltBackwards - Rights insufficient
        Assertions.assertThrows(RuntimeException.class, () -> {
            try {
                build.getBaggageScanner().moveBeltBackwards();
            } catch (NoSuchElementException | NullPointerException ex) {
                //These two exceptions are expected for this test.
            }
        });

        //Scan - Rights insufficient
        Assertions.assertThrows(RuntimeException.class, () -> {
            try {
                build.getBaggageScanner().scan();
            } catch (NullPointerException ex) {
//expected Exception
            }
        });

        //Alarm - Rights insufficient
        build.getBaggageScanner().setCurrentState(new Activated());
        Assertions.assertThrows(RuntimeException.class, () -> {
            try {
                build.getBaggageScanner().alert();
            } catch (NoSuchElementException ex) {
//expected Exception
            }
        });

        //Report - Rights sufficient
        Assertions.assertDoesNotThrow(() -> build.getBaggageScanner().report());

        //Maintenance - Rights insufficient
        Assertions.assertThrows(RuntimeException.class, () -> build.getBaggageScanner().maintenance());

        //O
        build.getBaggageScanner().getOperatingStation().setAuthenticatedUserType("O");
        build.getBaggageScanner().setCurrentState(new Activated());

        //MoveBeltForward - Rights insufficient
        Assertions.assertThrows(RuntimeException.class, () -> {
            try {
                build.getBaggageScanner().moveBeltForward();
            } catch (NoSuchElementException | NullPointerException ex) {
                //Expected Exceptions
            }
        });

        //MoveBeltBackwards - Rights insufficient
        Assertions.assertThrows(RuntimeException.class, () -> {
            try {
                build.getBaggageScanner().moveBeltBackwards();
            } catch (NoSuchElementException | NullPointerException ex) {
                //These two exceptions are expected for this test.
            }
        });

        //Scan - Rights insufficient
        Assertions.assertThrows(RuntimeException.class, () -> {
            try {
                build.getBaggageScanner().scan();
            } catch (NullPointerException ex) {
//expected Exception
            }
        });

        //Alarm - Rights insufficient
        build.getBaggageScanner().setCurrentState(new Activated());
        Assertions.assertThrows(RuntimeException.class, () -> {
            try {
                build.getBaggageScanner().alert();
            } catch (NoSuchElementException ex) {
//expected Exception
            }
        });

        //Report - Rights insufficient
        Assertions.assertThrows(RuntimeException.class, () -> build.getBaggageScanner().report());

        //Maintenance - Rights insufficient
        Assertions.assertThrows(RuntimeException.class, () -> build.getBaggageScanner().maintenance());

        //T
        build.getBaggageScanner().getOperatingStation().setAuthenticatedUserType("T");
        build.getBaggageScanner().setCurrentState(new Activated());

        //MoveBeltForward - Rights insufficient
        Assertions.assertThrows(RuntimeException.class, () -> {
            try {
                build.getBaggageScanner().moveBeltForward();
            } catch (NoSuchElementException | NullPointerException ex) {
                //Expected Exceptions
            }
        });

        //MoveBeltBackwards - Rights insufficient
        Assertions.assertThrows(RuntimeException.class, () -> {
            try {
                build.getBaggageScanner().moveBeltBackwards();
            } catch (NoSuchElementException | NullPointerException ex) {
                //These two exceptions are expected for this test.
            }
        });
        //Scan - Rights insufficient
        Assertions.assertThrows(RuntimeException.class, () -> {
            try {
                build.getBaggageScanner().scan();
            } catch (NullPointerException ex) {
//expected Exception
            }
        });

        //Alarm - Rights insufficient
        build.getBaggageScanner().setCurrentState(new Activated());
        Assertions.assertThrows(RuntimeException.class, () -> {
            try {
                build.getBaggageScanner().alert();
            } catch (NoSuchElementException ex) {
//expected Exception
            }
        });

        //Report - Rights insufficient
        Assertions.assertThrows(RuntimeException.class, () -> build.getBaggageScanner().report());

        //Maintenance - Rights sufficient
        Assertions.assertDoesNotThrow(() -> build.getBaggageScanner().maintenance());

        //K
        build.getBaggageScanner().getOperatingStation().setAuthenticatedUserType("K");
        build.getBaggageScanner().setCurrentState(new Activated());

        //MoveBeltForward - Rights insufficient
        Assertions.assertThrows(RuntimeException.class, () -> {
            try {
                build.getBaggageScanner().moveBeltForward();
            } catch (NoSuchElementException | NullPointerException ex) {
                //Expected Exceptions
            }
        });

        //MoveBeltBackwards - Rights insufficient
        Assertions.assertThrows(RuntimeException.class, () -> {
            try {
                build.getBaggageScanner().moveBeltBackwards();
            } catch (NoSuchElementException | NullPointerException ex) {
                //These two exceptions are expected for this test.
            }
        });

        //Scan - Rights insufficient
        Assertions.assertThrows(RuntimeException.class, () -> {
            try {
                build.getBaggageScanner().scan();
            } catch (NullPointerException ex) {
//expected Exception
            }
        });

        //Alarm - Rights insufficient
        build.getBaggageScanner().setCurrentState(new Activated());
        Assertions.assertThrows(RuntimeException.class, () -> {
            try {
                build.getBaggageScanner().alert();
            } catch (NoSuchElementException ex) {
//expected Exception
            }
        });

        //Report - Rights insufficient
        Assertions.assertThrows(RuntimeException.class, () -> build.getBaggageScanner().report());

        //Maintenance - Rights insufficient
        Assertions.assertThrows(RuntimeException.class, () -> build.getBaggageScanner().maintenance());

    }

    @Order(6)
    @Test
    public void test6_sOnlyUnlock () {
        //BaggageScanner automatically unlocks, when a Supervisor logs in
        Locked locked = new Locked();
        build.getBaggageScanner().setCurrentState(locked);
        //I
        build.getBaggageScanner().getOperatingStation().setAuthenticatedUserType("I");
        Assertions.assertTrue(build.getBaggageScanner().getCurrentState() instanceof Locked);
        build.getBaggageScanner().setCurrentState(locked);
        //S
        build.getBaggageScanner().getOperatingStation().setAuthenticatedUserType("S");
        Assertions.assertFalse(build.getBaggageScanner().getCurrentState() instanceof Locked);
        build.getBaggageScanner().setCurrentState(locked);
        //O
        build.getBaggageScanner().getOperatingStation().setAuthenticatedUserType("O");
        Assertions.assertTrue(build.getBaggageScanner().getCurrentState() instanceof Locked);
        build.getBaggageScanner().setCurrentState(locked);
        //T
        build.getBaggageScanner().getOperatingStation().setAuthenticatedUserType("T");
        Assertions.assertTrue(build.getBaggageScanner().getCurrentState() instanceof Locked);
        build.getBaggageScanner().setCurrentState(locked);
        //K
        build.getBaggageScanner().getOperatingStation().setAuthenticatedUserType("K");
        Assertions.assertTrue(build.getBaggageScanner().getCurrentState() instanceof Locked);
    }

    @Order(7)
    @Test
    public void test7_recogniseKnife () {
        // Turn Scanner on
        ((Supervisor) build.getBaggageScanner().getSupervision().getSupervisor()).switchPower(build.getBaggageScanner());

        // Activate Scanner
        build.getBaggageScanner().getOperatingStation().getPresentUser().enterPIN(build.getBaggageScanner().getOperatingStation().getCardReader());

        for (int i = 0 ; i < 6 ; i++)
            build.getBaggageScanner().getTraySupplyer().getPassengerQueue().remove();
        build.getBaggageScanner().getTraySupplyer().nextPassenger();

        ((Inspector) build.getEmployees().get("I1")).pushTray(build.getBaggageScanner());

        ((Inspector) build.getEmployees().get("I2")).pushButton(build.getBaggageScanner().getOperatingStation().getButtons()[2]);
        ((Inspector) build.getEmployees().get("I2")).pushButton(build.getBaggageScanner().getOperatingStation().getButtons()[1]);
        ((Inspector) build.getEmployees().get("I2")).pushButton(build.getBaggageScanner().getOperatingStation().getButtons()[2]);

        // Maintenance
        build.getEmployees().get("T").enterPIN(build.getBaggageScanner().getOperatingStation().getCardReader());
        ((Technician) build.getEmployees().get("T")).performMaintenance(build.getBaggageScanner());

        // Turn off and get the report
        ((Supervisor) build.getEmployees().get("S")).switchPower(build.getBaggageScanner());
        build.getEmployees().get("S").enterPIN(build.getBaggageScanner().getOperatingStation().getCardReader());
        build.getBaggageScanner().report();

        //ersetzen durch simulation.run -> get(12313)

        Assertions.assertEquals(ProhibitedItem.KNIFE, build.getBaggageScanner().getScanResults().getFirst().getResult().getItemType());
    }

    @Order(8)
    @Test
    public void test8_recogniseWeapon () {
        // Turn Scanner on
        ((Supervisor) build.getBaggageScanner().getSupervision().getSupervisor()).switchPower(build.getBaggageScanner());

        // Activate Scanner
        build.getBaggageScanner().getOperatingStation().getPresentUser().enterPIN(build.getBaggageScanner().getOperatingStation().getCardReader());

        for (int i = 0 ; i < 14 ; i++)
            build.getBaggageScanner().getTraySupplyer().getPassengerQueue().remove();
        build.getBaggageScanner().getTraySupplyer().nextPassenger();

        ((Inspector) build.getEmployees().get("I1")).pushTray(build.getBaggageScanner());
        while (!build.getBaggageScanner().getBelt().getTrayQueue().isEmpty()) {
            ((Inspector) build.getEmployees().get("I2")).pushButton(build.getBaggageScanner().getOperatingStation().getButtons()[2]);
            ((Inspector) build.getEmployees().get("I2")).pushButton(build.getBaggageScanner().getOperatingStation().getButtons()[1]);
        }
        ((Inspector) build.getEmployees().get("I2")).pushButton(build.getBaggageScanner().getOperatingStation().getButtons()[2]);

        // Maintenance
        build.getEmployees().get("T").enterPIN(build.getBaggageScanner().getOperatingStation().getCardReader());
        ((Technician) build.getEmployees().get("T")).performMaintenance(build.getBaggageScanner());

        // Turn off and get the report
        ((Supervisor) build.getEmployees().get("S")).switchPower(build.getBaggageScanner());
        build.getEmployees().get("S").enterPIN(build.getBaggageScanner().getOperatingStation().getCardReader());
        build.getBaggageScanner().report();

        Assertions.assertEquals(ProhibitedItem.WEAPON, build.getBaggageScanner().getScanResults().get(1).getResult().getItemType());
    }

    @Order(9)
    @Test
    public void test9_recogniseExplosive () {
        // Turn Scanner on
        ((Supervisor) build.getBaggageScanner().getSupervision().getSupervisor()).switchPower(build.getBaggageScanner());

        // Activate Scanner
        build.getBaggageScanner().getOperatingStation().getPresentUser().enterPIN(build.getBaggageScanner().getOperatingStation().getCardReader());

        for (int i = 0 ; i < 331 ; i++)
            build.getBaggageScanner().getTraySupplyer().getPassengerQueue().remove();
        build.getBaggageScanner().getTraySupplyer().nextPassenger();

        ((Inspector) build.getEmployees().get("I1")).pushTray(build.getBaggageScanner());
        while (!build.getBaggageScanner().getBelt().getTrayQueue().isEmpty()) {
            ((Inspector) build.getEmployees().get("I2")).pushButton(build.getBaggageScanner().getOperatingStation().getButtons()[2]);
            ((Inspector) build.getEmployees().get("I2")).pushButton(build.getBaggageScanner().getOperatingStation().getButtons()[1]);
        }
        ((Inspector) build.getEmployees().get("I2")).pushButton(build.getBaggageScanner().getOperatingStation().getButtons()[2]);

        // Maintenance
        build.getEmployees().get("T").enterPIN(build.getBaggageScanner().getOperatingStation().getCardReader());
        ((Technician) build.getEmployees().get("T")).performMaintenance(build.getBaggageScanner());

        // Turn off and get the report
        ((Supervisor) build.getEmployees().get("S")).switchPower(build.getBaggageScanner());
        build.getEmployees().get("S").enterPIN(build.getBaggageScanner().getOperatingStation().getCardReader());
        build.getBaggageScanner().report();

        Assertions.assertEquals(ProhibitedItem.EXPLOSIVE, build.getBaggageScanner().getScanResults().get(0).getResult().getItemType());
    }

    @Order(10)
    @Test
    public void test10_log () throws IOException {
        build.runSimulation();
        String log = Files.readString(Path.of("log.txt"));
        for (int i = 0 ; i < build.getBaggageScanner().getScanResults().size() ; i++) {
            Assertions.assertTrue(log.contains(build.getBaggageScanner().getScanResults().get(i).toString()));
        }
    }

    @Order(11)
    @Test
    public void test11_procedureNothingFound () {
        Assertions.assertTrue(TestUtility.correctProcedureNoProhibitedItems());
    }

    @Order(12)
    @Test
    public void test12_procedureKnifeFound () {
        Assertions.assertTrue(TestUtility.correctProcedureKnife());
    }

    @Order(13)
    @Test
    public void test13_procedureWeaponFound () {
        //Going to suiting Passenger
        Assertions.assertTrue(TestUtility.correctProcedureWeapon());
    }

    @Order(14)
    @Test
    public void test14_procedureExplosiveFound () {
        //Going to suiting Passenger
        Assertions.assertTrue(TestUtility.correctProcedureExplosive());
    }
}
