package security.components;

import security.customer.Passenger;
import security.data.Record;
import security.data.enums.ScanResultType;
import security.staff.Employee;
import security.staff.FederalPoliceOfficer;
import security.state.Shutdown;
import security.state.State;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.LinkedList;

public class BaggageScanner implements IBaggageScanner {

    //region Data/Information
    private final LinkedList<Record> scanResults = new LinkedList<>(); // Linked List for getLast()
    private final HashMap<String, Byte> permissions;
    private State currentState = new Shutdown();
    //endregion Data/Information
    //region Components
    private TraySupplyer traySupplyer;
    private RollerConveyor rollerConveyor;
    private Belt belt;
    private Scanner scanner;
    private Track[] outgoingTracks;
    private ManualPostControl manualPostControl;

    private OperatingStation operatingStation;
    private Supervision supervision;
    //endregion Components
    private Employee currentFederalPoliceOfficer;


    public BaggageScanner (HashMap<String, Byte> permissions) {
        this.permissions = permissions;
    }


    @Override
    public void moveBeltForward () {
        //region Check for rights
        String authenticated = operatingStation.getAuthenticatedUserType();
        byte permission = permissions.get(authenticated);
        if ((permission & 1) == 0) {
            System.out.println("Bag. Scanner: \u001B[0;32m*** WARNING: RIGHTS NOT SUFFICIENT ***\u001B[0m");
            throw new RuntimeException("Current Rights are not sufficient");
        }
        //endregion Check for rights

        System.out.println("Bag. Scanner: Moving Belt forwards");
        Tray temp = belt.moveRight();
        temp = scanner.move(temp);

        if (temp != null) {
            if (scanResults.getLast().getResult().getType() == ScanResultType.CLEAN) {
                outgoingTracks[1].trayArrive(temp);
            } else {
                // Should not happen. A dangerous item is forwarded directly after the scan. This is just a failsafe.
                outgoingTracks[0].trayArrive(temp);
            }
        }
    }

    @Override
    public void moveBeltBackwards () {
        //region Check for rights
        String authenticated = operatingStation.getAuthenticatedUserType();
        byte permission = permissions.get(authenticated);
        if ((permission & 1 << 1) == 0) {
            System.out.println("Bag. Scanner: \u001B[0;32m*** WARNING: RIGHTS NOT SUFFICIENT ***\u001B[0m");
            throw new RuntimeException("Current Rights are not sufficient");
        }
        //endregion Check for rights

        System.out.println("Bag. Scanner: Moving Belt backwards");

        // Nothing is taken back from Track02. only from Track01/ManualPostControl.
        Tray temp;
        temp = outgoingTracks[0].getTrays().removeLast();
        temp = scanner.move(temp);
        if (temp != null)
            belt.moveBackwards(temp);
    }

    @Override
    public void scan () {
        //region Check for rights
        String authenticated = operatingStation.getAuthenticatedUserType();
        byte permission = permissions.get(authenticated);
        if ((permission & 1 << 2) == 0) {
            System.out.println("Bag. Scanner: \u001B[0;32m*** WARNING: RIGHTS NOT SUFFICIENT ***\u001B[0m");
            throw new RuntimeException("Current Rights are not sufficient");
        }
        //endregion Check for rights

        currentState = currentState.scan();
        System.out.println("Bag. Scanner: Starting Scan Procedure");
        scanResults.add(scanner.scan());
        currentState = currentState.scanDone();
    }

    @Override
    public void alert () {
        //region Check for rights
        String authenticated = operatingStation.getAuthenticatedUserType();
        byte permission = permissions.get(authenticated);
        if ((permission & 1 << 3) == 0) {
            System.out.println("Bag. Scanner: \u001B[0;32m*** WARNING: RIGHTS NOT SUFFICIENT ***\u001B[0m");
            throw new RuntimeException("Current Rights are not sufficient");
        }
        //endregion Check for rights

        System.out.println("Bag. Scanner: Alert triggered");
        System.out.println("              \u001B[1;31m**********!!SCANNER ALERT!!**********\u001B[0m"); // Red,Bold ANSI Color
        currentState = currentState.lock();

        // Get the needed people
        final FederalPoliceOfficer policeOfficer = (FederalPoliceOfficer) this.currentFederalPoliceOfficer;
        final Passenger susPassenger = manualPostControl.getBelongingTrack().getTrays().getLast().getContainedBaggage().getOwner();

        // Get the people where they need to be
        if (!susPassenger.isArrested()) {
            outgoingTracks[1].callPassenger(susPassenger);
            policeOfficer.arrestPassenger(susPassenger);
        }
        manualPostControl.setPresentPassenger(susPassenger);
        manualPostControl.setPresentOfficers(new FederalPoliceOfficer[3]);
        manualPostControl.getPresentOfficers()[0] = policeOfficer;

        FederalPoliceOfficer[] reinforcement = policeOfficer.getOffice().requestReinforcement();
        for (int i = 0 ; i < reinforcement.length ; i++) {
            manualPostControl.getPresentOfficers()[i + 1] = reinforcement[i];
        }
    }

    @Override
    public void report () {
        //region Check for rights
        String authenticated = operatingStation.getAuthenticatedUserType();
        byte permission = permissions.get(authenticated);
        if ((permission & 1 << 4) == 0) {
            System.out.println("Bag. Scanner: \u001B[0;32m*** WARNING: RIGHTS NOT SUFFICIENT ***\u001B[0m");
            throw new RuntimeException("Current Rights are not sufficient");
        }
        //endregion Check for rights

        DecimalFormat format = new DecimalFormat("0000");

        System.out.println("Bag. Scanner: Generating Report");
        // Header
        System.out.println("REPORT      : #########################");
        System.out.println("REPORT      : #### SCANNER REPORT #####");
        System.out.println("REPORT      : #########################");

        // General Data
        System.out.println("REPORT      : # Current-State:        #");
        System.out.printf("REPORT      : # %s #%n", currentState.toString());
        System.out.printf("REPORT      : # Performed-Scans: %s #%n", format.format(scanResults.size()));
        System.out.println("REPORT      : #########################");

        // Scan Results
        System.out.println("REPORT      : ##### SCAN RESULTS ######");
        System.out.println("REPORT      : #                       #");
        scanResults.forEach(System.out::println);
        System.out.println("REPORT      : #                       #");

        // Footer
        System.out.println("REPORT      : #########################");
        System.out.println("REPORT      : ##### END OF REPORT #####");
        System.out.println("REPORT      : #########################");
    }

    @Override
    public void maintenance () {
        //region Check for rights
        String authenticated = operatingStation.getAuthenticatedUserType();
        byte permission = permissions.get(authenticated);
        if ((permission & 1 << 5) == 0) {
            System.out.println("Bag. Scanner: \u001B[0;32m*** WARNING: RIGHTS NOT SUFFICIENT ***\u001B[0m");
            throw new RuntimeException("Current Rights are not sufficient");
        }
        //endregion Check for rights

        currentState = currentState.allScansDone();
        // Placebo Messages ;-)
        System.out.println("Bag. Scanner: Performing Maintenance");
        System.out.println("Bag. Scanner: Transmitting data...");
        System.out.println("Bag. Scanner: Checking for errors...");
        System.out.println("Bag. Scanner: No errors detected.");
        System.out.println("Bag. Scanner: Maintenance complete!");
        System.out.println("Bag. Scanner: Ready for Shutdown");
    }


    public LinkedList<Record> getScanResults () {
        // Return a copy to prevent modification of the original (Collections.unmodifiableList does not give a LinkedList)
        return new LinkedList<>(scanResults);
    }


    public State getCurrentState () {
        return currentState;
    }

    public void setCurrentState (State currentState) {
        this.currentState = currentState;
    }


    public HashMap<String, Byte> getPermissions () {
        return permissions;
    }


    public Employee getCurrentFederalPoliceOfficer () {
        return currentFederalPoliceOfficer;
    }

    public void setCurrentFederalPoliceOfficer (Employee currentFederalPoliceOfficer) {
        this.currentFederalPoliceOfficer = currentFederalPoliceOfficer;
    }


    public TraySupplyer getTraySupplyer () {
        return traySupplyer;
    }

    public void setTraySupplyer (TraySupplyer traySupplyer) {
        this.traySupplyer = traySupplyer;
    }


    public RollerConveyor getRollerConveyor () {
        return rollerConveyor;
    }

    public void setRollerConveyor (RollerConveyor rollerConveyor) {
        this.rollerConveyor = rollerConveyor;
    }


    public Belt getBelt () {
        return belt;
    }

    public void setBelt (Belt belt) {
        this.belt = belt;
    }


    public Scanner getScanner () {
        return scanner;
    }

    public void setScanner (Scanner scanner) {
        this.scanner = scanner;
    }


    public Track[] getOutgoingTracks () {
        return outgoingTracks;
    }

    public void setOutgoingTracks (Track[] outgoingTracks) {
        this.outgoingTracks = outgoingTracks;
    }


    public ManualPostControl getManualPostControl () {
        return manualPostControl;
    }

    public void setManualPostControl (ManualPostControl manualPostControl) {
        this.manualPostControl = manualPostControl;
    }


    public OperatingStation getOperatingStation () {
        return operatingStation;
    }

    public void setOperatingStation (OperatingStation operatingStation) {
        this.operatingStation = operatingStation;
    }


    public Supervision getSupervision () {
        return supervision;
    }

    public void setSupervision (Supervision supervision) {
        this.supervision = supervision;
    }
}
