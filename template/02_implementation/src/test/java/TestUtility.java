import org.junit.jupiter.api.Assertions;
import security.data.ScanResult;
import security.data.enums.ProhibitedItem;
import security.data.enums.ScanResultType;
import security.simulation.Simulation;
import security.staff.Inspector;
import security.staff.Supervisor;
import security.staff.Technician;

import java.io.IOException;
import java.net.URISyntaxException;

public class TestUtility {

    public static boolean correctProcedureExplosive () {
        boolean correct = false;

        Simulation.Builder simulationBuilder = new Simulation.Builder();
        simulationBuilder.defaultEmployees();
        try {
            simulationBuilder.defaultPassengers();
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
        Simulation build = simulationBuilder.build();
        build.initializeSimulation();

        // Turn Scanner on
        ((Supervisor) build.getBaggageScanner().getSupervision().getSupervisor()).switchPower(build.getBaggageScanner());

        // Activate Scanner
        build.getBaggageScanner().getOperatingStation().getPresentUser().enterPIN(build.getBaggageScanner().getOperatingStation().getCardReader());

        for (int i = 0 ; i < 332 ; i++) { //taking 332th Passenger instead of creating new Passenger for testing, since the implementation is already finished
            System.out.println("Simulation  : Next passenger is going through the scanner");
            build.getBaggageScanner().getTraySupplyer().nextPassenger();
            ((Inspector) build.getEmployees().get("I1")).pushTray(build.getBaggageScanner());

            while (!build.getBaggageScanner().getBelt().getTrayQueue().isEmpty()) {
                ((Inspector) build.getEmployees().get("I2")).pushButton(build.getBaggageScanner().getOperatingStation().getButtons()[2]);
                ((Inspector) build.getEmployees().get("I2")).pushButton(build.getBaggageScanner().getOperatingStation().getButtons()[1]);
            }
            ((Inspector) build.getEmployees().get("I2")).pushButton(build.getBaggageScanner().getOperatingStation().getButtons()[2]);
            System.out.println("Simulation  : Passenger Baggage was completely scanned");
            System.out.println();
        }

        //I2 reacted
        //  alert()
        //  Track 01
        //  I3.testBaggageForExplosives()
        //  inspectorReactedTo=3

        //      alert()
        //          currentSate.lock()
        //          arrests Passenger
        //          gets Reinforcements O2 and O3

        //      testBaggageForExplosives()
        //          I3 swipes Baggage
        //          tests stripe
        //              if Explosive is found
        //                  O3 steerRobot()
        //          Officers leaving with baggage and Passenger
        //          unlock - if it is needed for scan of other baggage
        //          scan of further baggage if needed and handing it over to 03

        //                      steerRobot()
        //                          robot.destroyBaggage

        //If inspectorReactedTo==3 I2 reacted to the prohibitedItem Explosive, activated the alarm and testBaggageForExplosives() of I3 was called for the last Passenger
        if(((Inspector) build.getEmployees().get("I2")).getInspectorReactedTo()==3)
            correct=true;

        // Maintenance
        build.getEmployees().get("T").enterPIN(build.getBaggageScanner().getOperatingStation().getCardReader());
        ((Technician) build.getEmployees().get("T")).performMaintenance(build.getBaggageScanner());

        // Turn off and get the report
        ((Supervisor) build.getEmployees().get("S")).switchPower(build.getBaggageScanner());
        build.getEmployees().get("S").enterPIN(build.getBaggageScanner().getOperatingStation().getCardReader());
        build.getBaggageScanner().report();

        ScanResult result = build.getBaggageScanner().getScanResults().getLast().getResult();

        if (!result.getType().equals(ScanResultType.PROHIBITED_ITEM)) {
            correct = false;
        }

        if (result.getItemType() == null || !result.getItemType().equals(ProhibitedItem.EXPLOSIVE)) {
            correct = false;
        }


        return correct;
    }

    public static boolean correctProcedureKnife () {
        boolean correct = false;

        Simulation.Builder simulationBuilder = new Simulation.Builder();
        simulationBuilder.defaultEmployees();
        try {
            simulationBuilder.defaultPassengers();
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
        Simulation build = simulationBuilder.build();
        build.initializeSimulation();

        // Turn Scanner on
        ((Supervisor) build.getBaggageScanner().getSupervision().getSupervisor()).switchPower(build.getBaggageScanner());

        // Activate Scanner
        build.getBaggageScanner().getOperatingStation().getPresentUser().enterPIN(build.getBaggageScanner().getOperatingStation().getCardReader());

        for (int i=0; i<7; i++) {   //taking 7th Passenger instead of creating new Passenger for testing, since the implementation is already finished
            System.out.println("Simulation  : Next passenger is going through the scanner");
            build.getBaggageScanner().getTraySupplyer().nextPassenger();
            ((Inspector) build.getEmployees().get("I1")).pushTray(build.getBaggageScanner());

            while (!build.getBaggageScanner().getBelt().getTrayQueue().isEmpty()) {
                ((Inspector) build.getEmployees().get("I2")).pushButton(build.getBaggageScanner().getOperatingStation().getButtons()[2]);
                ((Inspector) build.getEmployees().get("I2")).pushButton(build.getBaggageScanner().getOperatingStation().getButtons()[1]);
            }
            ((Inspector) build.getEmployees().get("I2")).pushButton(build.getBaggageScanner().getOperatingStation().getButtons()[2]);
            System.out.println("Simulation  : Passenger Baggage was completely scanned");
            System.out.println();
        }
        //Inspector.notifyKnife()
        //  Notification of Inspector I3
        //  Method sets output to Track 1
        //  Calls K
        //  Removes Knife
        //  puts Tray back
        //  rescans

        //if notifyKnife() was called in the last Passenger inspectorReactedTo==1
        if(((Inspector) build.getEmployees().get("I2")).getInspectorReactedTo()==1)
            correct=true;


        // Maintenance
        build.getEmployees().get("T").enterPIN(build.getBaggageScanner().getOperatingStation().getCardReader());
        ((Technician) build.getEmployees().get("T")).performMaintenance(build.getBaggageScanner());

        // Turn off and get the report
        ((Supervisor) build.getEmployees().get("S")).switchPower(build.getBaggageScanner());
        build.getEmployees().get("S").enterPIN(build.getBaggageScanner().getOperatingStation().getCardReader());
        build.getBaggageScanner().report();

        return correct;
    }

    public static boolean correctProcedureNoProhibitedItems () {
        boolean correct = false;

        Simulation.Builder simulationBuilder = new Simulation.Builder();
        simulationBuilder.defaultEmployees();
        try {
            simulationBuilder.defaultPassengers();
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
        Simulation build = simulationBuilder.build();
        build.initializeSimulation();

        //should do everything correctly for first Passenger Faraz Abbasi;0
        // Turn Scanner on
        ((Supervisor) build.getBaggageScanner().getSupervision().getSupervisor()).switchPower(build.getBaggageScanner());

        // Activate Scanner
        build.getBaggageScanner().getOperatingStation().getPresentUser().enterPIN(build.getBaggageScanner().getOperatingStation().getCardReader());

        // Scan first passengers baggage

        build.getBaggageScanner().getTraySupplyer().nextPassenger();
        ((Inspector) build.getEmployees().get("I1")).pushTray(build.getBaggageScanner());

        ((Inspector) build.getEmployees().get("I2")).pushButton(build.getBaggageScanner().getOperatingStation().getButtons()[2]); //right
        ((Inspector) build.getEmployees().get("I2")).pushButton(build.getBaggageScanner().getOperatingStation().getButtons()[1]); //scan

        ((Inspector) build.getEmployees().get("I2")).pushButton(build.getBaggageScanner().getOperatingStation().getButtons()[2]);

        //If inspectorReactedTo==0 everything went normally and nothing was found
        if(((Inspector) build.getEmployees().get("I2")).getInspectorReactedTo()==0)
            correct=true;

        // Maintenance
        build.getEmployees().get("T").enterPIN(build.getBaggageScanner().getOperatingStation().getCardReader());
        ((Technician) build.getEmployees().get("T")).performMaintenance(build.getBaggageScanner());

        // Turn off and get the report
        ((Supervisor) build.getEmployees().get("S")).switchPower(build.getBaggageScanner());
        build.getEmployees().get("S").enterPIN(build.getBaggageScanner().getOperatingStation().getCardReader());
        build.getBaggageScanner().report();

        if (!build.getBaggageScanner().getScanResults().getLast().getResult().getType().equals(ScanResultType.CLEAN)) {
            correct = false;
        }

        return correct;
    }

    public static boolean correctProcedureWeapon () {
        boolean correct = false;

        Simulation.Builder simulationBuilder = new Simulation.Builder();
        simulationBuilder.defaultEmployees();
        try {
            simulationBuilder.defaultPassengers();
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
        Simulation build = simulationBuilder.build();
        build.initializeSimulation();

        // Turn Scanner on
        ((Supervisor) build.getBaggageScanner().getSupervision().getSupervisor()).switchPower(build.getBaggageScanner());

        // Activate Scanner
        build.getBaggageScanner().getOperatingStation().getPresentUser().enterPIN(build.getBaggageScanner().getOperatingStation().getCardReader());

        for (int i = 0 ; i < 15 ; i++) { //taking 15th Passenger instead of creating new Passenger for testing, since the implementation is already finished
            System.out.println("Simulation  : Next passenger is going through the scanner");
            build.getBaggageScanner().getTraySupplyer().nextPassenger();
            ((Inspector) build.getEmployees().get("I1")).pushTray(build.getBaggageScanner());

            while (!build.getBaggageScanner().getBelt().getTrayQueue().isEmpty()) {
                ((Inspector) build.getEmployees().get("I2")).pushButton(build.getBaggageScanner().getOperatingStation().getButtons()[2]);
                ((Inspector) build.getEmployees().get("I2")).pushButton(build.getBaggageScanner().getOperatingStation().getButtons()[1]);
            }
            ((Inspector) build.getEmployees().get("I2")).pushButton(build.getBaggageScanner().getOperatingStation().getButtons()[2]);
            System.out.println("Simulation  : Passenger Baggage was completely scanned");
            System.out.println();
        }

        //I2 reacted
        //  alert()
        //  Track 01
        //  O1.notifyWeapon()
        //  inspectorReactedTo=2

        //      alert()
        //          currentSate.lock()
        //          arrests Passenger
        //          gets Reinforcements O2 and O3

        //      notifyWeapon()
        //          Notification of FederalPoliceOfficer O1
        //          takes Weapon
        //          gives weapon to O3
        //          unlock - since it is needed for scan of other baggage
        //          scan of further baggage if needed and handing it over to 03
        //          Officers leaving with baggage and Passenger

        //If inspectorReactedTo==2 I2 reacted to the prohibitedItem Weapon activated the alarm and notifyWeapon() of O1 was called for the last Passenger
        if(((Inspector) build.getEmployees().get("I2")).getInspectorReactedTo()==2)
            correct=true;

        // Maintenance
        build.getEmployees().get("T").enterPIN(build.getBaggageScanner().getOperatingStation().getCardReader());
        ((Technician) build.getEmployees().get("T")).performMaintenance(build.getBaggageScanner());

        // Turn off and get the report
        ((Supervisor) build.getEmployees().get("S")).switchPower(build.getBaggageScanner());
        build.getEmployees().get("S").enterPIN(build.getBaggageScanner().getOperatingStation().getCardReader());
        build.getBaggageScanner().report();

        ScanResult result = build.getBaggageScanner().getScanResults().get(18).getResult();

        if(!result.getType().equals(ScanResultType.PROHIBITED_ITEM)){
            correct = false;
        }

        if (result.getItemType() == null || !result.getItemType().equals(ProhibitedItem.WEAPON)) {
            correct = false;
        }


        return correct;
    }
}
