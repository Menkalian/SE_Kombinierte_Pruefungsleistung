package security.simulation;

import security.data.ScanResult;
import security.data.enums.ProhibitedItem;
import security.data.enums.ScanResultType;
import security.staff.Inspector;
import security.staff.Supervisor;
import security.staff.Technician;

import java.awt.desktop.AboutEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URISyntaxException;

public class TestUtility {

    public static boolean correctProcedureNoProhibitedItems(){
        boolean correct = true;

        /*try {
            System.setOut(new PrintStream(new File("log.txt")));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }*/
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

            build.getBaggageScanner().getTraySupplement().nextPassenger();
            ((Inspector) build.getEmployees().get("I1")).pushTray(build.getBaggageScanner());

                ((Inspector) build.getEmployees().get("I2")).pushButton(build.getBaggageScanner().getOperatingStation().getButtons()[2]); //right
                ((Inspector) build.getEmployees().get("I2")).pushButton(build.getBaggageScanner().getOperatingStation().getButtons()[1]); //scan

                ((Inspector) build.getEmployees().get("I2")).pushButton(build.getBaggageScanner().getOperatingStation().getButtons()[2]);


        // Maintenance
        build.getEmployees().get("T").enterPIN(build.getBaggageScanner().getOperatingStation().getCardReader());
        ((Technician) build.getEmployees().get("T")).performMaintenance(build.getBaggageScanner());

        // Turn off and get the report
        ((Supervisor) build.getEmployees().get("S")).switchPower(build.getBaggageScanner());
        build.getEmployees().get("S").enterPIN(build.getBaggageScanner().getOperatingStation().getCardReader());
        build.getBaggageScanner().report();

        if(!build.getBaggageScanner().getScanResults().getLast().getResult().getType().equals(ScanResultType.CLEAN)){
            correct = false;
        }

        return correct;
    }

    public static boolean correctProcedureKnife () {
        boolean correct = true;

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

        for (int i=0; i<7; i++) {
            System.out.println("Simulation  : Next passenger is going through the scanner");
            build.getBaggageScanner().getTraySupplement().nextPassenger();
            ((Inspector) build.getEmployees().get("I1")).pushTray(build.getBaggageScanner());

            while (!build.getBaggageScanner().getBelt().getTrayQueue().isEmpty()) {
                ((Inspector) build.getEmployees().get("I2")).pushButton(build.getBaggageScanner().getOperatingStation().getButtons()[2]);
                ((Inspector) build.getEmployees().get("I2")).pushButton(build.getBaggageScanner().getOperatingStation().getButtons()[1]);
            }
            ((Inspector) build.getEmployees().get("I2")).pushButton(build.getBaggageScanner().getOperatingStation().getButtons()[2]);
            System.out.println("Simulation  : Passenger Baggage was completely scanned");
            System.out.println();
        }


        // Maintenance
        build.getEmployees().get("T").enterPIN(build.getBaggageScanner().getOperatingStation().getCardReader());
        ((Technician) build.getEmployees().get("T")).performMaintenance(build.getBaggageScanner());

        // Turn off and get the report
        ((Supervisor) build.getEmployees().get("S")).switchPower(build.getBaggageScanner());
        build.getEmployees().get("S").enterPIN(build.getBaggageScanner().getOperatingStation().getCardReader());
        build.getBaggageScanner().report();

        ScanResult result = build.getBaggageScanner().getScanResults().get(7).getResult();

        if(!result.getType().equals(ScanResultType.PROHIBITED_ITEM)){
            correct = false;
        }

        if(result.getItemType() == null || !result.getItemType().equals(ProhibitedItem.KNIFE)){
            correct = false;
        }


        return correct;
    }

    //swipe
    //enterPIN
        //activated
    //Passenger takes Tray
    //puts Baggage
    //I1 push
    //I2 press right
    //moves
    //I2 press square
    //scan
        //inUse
        //activated
        //log Record
    //clean
        //track 02

    //forbidden
        //Knife

        //Gun

        //Explosive
}
