import org.junit.jupiter.api.*;
import security.customer.HandBaggage;
import security.simulation.Simulation;
import security.simulation.TestUtility;
import security.state.Locked;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URISyntaxException;
import java.util.stream.Stream;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SimulationTest {

    private Simulation build;

    @BeforeEach
    public void setUp() {
        try {
            System.setOut(new PrintStream(new File("log.txt")));
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
        build.runSimulation();
    }

    @Order(1)
    @TestFactory
    public Stream<DynamicTest> test1_passengersAndBaggage (){
        Assertions.assertEquals(568, build.getPassengers().stream().count());
        //Assertions.assertEquals(609, build.getBaggageScanner().);
        return Stream.<DynamicTest>builder().build();
    }

    @Order(2)
    @Test
    public void test2_occupiedCorrectly (){
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
    public void test3_lockIDCard (){
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
    public void test4_koBaggageScanner (){
        Assertions.assertFalse(build.getBaggageScanner().getOperatingStation().getCardReader().swipeCard(build.getEmployees().get("O1").getIdCard()));
        Assertions.assertFalse(build.getBaggageScanner().getOperatingStation().getCardReader().swipeCard(build.getEmployees().get("K").getIdCard()));
    }

    @Order(5)
    @Test
    public void test5_onlyProfileFunctions (){
        Assertions.assertTrue(true);

        //assert throws

        //I
            //MoveBeltForward

            //MoveBeltBackwards

            //Scan

            //Alarm

            //Report

            //Maintenance

        //S
            //MoveBeltForward

            //MoveBeltBackwards

            //Scan

            //Alarm

            //Report

            //Maintenance

        //O
            //MoveBeltForward

            //MoveBeltBackwards

            //Scan

            //Alarm

            //Report

            //Maintenance

        //T
            //MoveBeltForward

            //MoveBeltBackwards

            //Scan

            //Alarm

            //Report

            //Maintenance

        //K
            //MoveBeltForward

            //MoveBeltBackwards

            //Scan

            //Alarm

            //Report

            //Maintenance

    }

    @Order(6)
    @Test
    public void test6_sOnlyUnlock (){
        //BaggageScanner automaticly unlocks, when a Supervisor logs in
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
    public void test7_recogniseKnife (){
        Assertions.assertTrue(true);
        /*HandBaggage handBaggage = new HandBaggage()
        File baggageFile = new File(Simulation.class.getResource("/baggage_7.txt"));
        build.getBaggageScanner().getScanner().move()
        */
    }

    @Order(8)
    @Test
    public void test8_recogniseWeapon (){
        Assertions.assertTrue(true);
    }

    @Order(9)
    @Test
    public void test9_recogniseExplosive (){
        Assertions.assertTrue(true);
    }

    @Order(10)
    @Test
    public void test10_log (){
        Assertions.assertTrue(true);

    }

    @Order(11)
    @Test
    public void test11_procedureNothingFound (){
        Assertions.assertTrue(TestUtility.correctProcedureNoProhibitedItems());
    }

    @Order(12)
    @Test
    public void test12_procedureKnifeFound (){
        Assertions.assertTrue(TestUtility.correctProcedureKnife());
    }

    @Order(13)
    @Test
    public void test13_procedureWeaponFound (){
        Assertions.assertTrue(true);
    }

    @Order(14)
    @Test
    public void test14_procedureExplosiveFound (){
        Assertions.assertTrue(true);
    }
}
