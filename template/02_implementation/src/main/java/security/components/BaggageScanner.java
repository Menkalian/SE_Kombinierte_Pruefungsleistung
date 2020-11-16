package security.components;

import security.customer.Passenger;
import security.data.Record;
import security.data.enums.ScanResultType;
import security.staff.Employee;
import security.staff.FederalPoliceOfficer;
import security.state.Shutdown;
import security.state.State;

import java.text.DecimalFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class BaggageScanner implements IBaggageScanner {
    private final List<Record> scanResults = new LinkedList<>();
    private final HashMap<String, Byte> permissions;
    private State currentState = new Shutdown();
    private Employee currentFederalPoliceOfficer;
    private TraySupplyment traySupplyment;
    private RollerConveyor rollerConveyor;
    private Belt belt;
    private Scanner scanner;
    private Track[] outgoingTracks;
    private ManualPostControl manualPostControl;
    private OperatingStation operatingStation;
    private Supervision supervision;

    public BaggageScanner (HashMap<String, Byte> permissions) {
        this.permissions = permissions;
    }

    public List<Record> getScanResults () {
        return Collections.unmodifiableList(scanResults);
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

    public TraySupplyment getTraySupplyment () {
        return traySupplyment;
    }

    public void setTraySupplyment (TraySupplyment traySupplyment) {
        this.traySupplyment = traySupplyment;
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

    @Override
    public void moveBeltForward () {
        // Check for rights
        String authenticated = operatingStation.getAuthenticatedUserType();
        byte permission = permissions.get(authenticated);
        // Readability/Understandability ->
        //noinspection PointlessBitwiseExpression
        if ((permission & 1 << 0) == 0) {
            System.out.println("*** WARNING: RIGHTS NOT SUFFICIENT ***");
            return;
        }

        Tray temp = belt.moveRight();
        temp = scanner.move(temp);
        if (temp != null) {
            if (scanResults.get(scanResults.size() - 1).getResult().getType() == ScanResultType.CLEAN) {
                outgoingTracks[1].trayArrive(temp);
            } else {
                // Should not happen. A dangerous item is forwarded directly after the scan. This is just a failsafe.
                outgoingTracks[0].trayArrive(temp);
            }
        }
        System.out.println("BaggageScanner moved Belt forwards.");
    }

    @Override
    public void moveBeltBackwards () {
        // Check for rights
        String authenticated = operatingStation.getAuthenticatedUserType();
        byte permission = permissions.get(authenticated);
        if ((permission & 1 << 1) == 0) {
            System.out.println("*** WARNING: RIGHTS NOT SUFFICIENT ***");
            return;
        }

        // Nothing is taken back from Track02. only from Track01/ManualPostControl.
        Tray temp = outgoingTracks[0].getTrays().remove(outgoingTracks[0].getTrays().size() - 1);
        temp = scanner.move(temp);
        if (temp != null)
            belt.moveBackwards(temp);
        System.out.println("BaggageScanner moved Belt backwards.");
    }

    @Override
    public void scan () {
        // Check for rights
        String authenticated = operatingStation.getAuthenticatedUserType();
        byte permission = permissions.get(authenticated);
        if ((permission & 1 << 2) == 0) {
            System.out.println("*** WARNING: RIGHTS NOT SUFFICIENT ***");
            return;
        }

        currentState = currentState.scan();
        System.out.println("Starting Scan Procedure.");
        scanResults.add(scanner.scan());
        currentState = currentState.scanDone();
    }

    @Override
    public void alert () {
        // Check for rights
        String authenticated = operatingStation.getAuthenticatedUserType();
        byte permission = permissions.get(authenticated);
        if ((permission & 1 << 3) == 0) {
            System.out.println("*** WARNING: RIGHTS NOT SUFFICIENT ***");
            return;
        }

        System.out.println("*****SCANNER ALERT!!!*****");
        currentState = currentState.lock();

        final FederalPoliceOfficer policeOfficer = (FederalPoliceOfficer) this.currentFederalPoliceOfficer;
        final Passenger susPassenger = scanner.getCurrentTray().getContainedBaggage().getOwner();

        if (!susPassenger.isArrested()) {
            outgoingTracks[1].callPassenger(susPassenger);
            policeOfficer.arrestPassenger(susPassenger);
        }
        manualPostControl.setPresentPassenger(susPassenger);
        manualPostControl.setPresentOfficers(new FederalPoliceOfficer[3]);
        manualPostControl.getPresentOfficers()[0] = policeOfficer;

        FederalPoliceOfficer[] reinforcement = policeOfficer.getOffice().requestReinforcment();
        for (int i = 0 ; i < reinforcement.length ; i++) {
            manualPostControl.getPresentOfficers()[i + 1] = reinforcement[i];
        }
    }

    @Override
    public void report () {
        // Check for rights
        String authenticated = operatingStation.getAuthenticatedUserType();
        byte permission = permissions.get(authenticated);
        if ((permission & 1 << 4) == 0) {
            System.out.println("*** WARNING: RIGHTS NOT SUFFICIENT ***");
            return;
        }

        DecimalFormat format = new DecimalFormat("0000");

        System.out.println("#########################");
        System.out.println("#### SCANNER REPORT #####");
        System.out.println("#########################");
        System.out.println("# Current-State:        #");
        System.out.println("# " + currentState.toString() + " #");
        System.out.println("# Performed-Scans: " + format.format(scanResults.size()) + "#");
        System.out.println("#########################");
        System.out.println("##### SCAN RESULTS ######");
        System.out.println("#                       #");
        scanResults.forEach(System.out::println);
        System.out.println("#                       #");
        System.out.println("#########################");
        System.out.println("##### END OF REPORT #####");
        System.out.println("#########################");
    }

    @Override
    public void maintenance () {
        // Check for rights
        String authenticated = operatingStation.getAuthenticatedUserType();
        byte permission = permissions.get(authenticated);
        if ((permission & 1 << 5) == 0) {
            System.out.println("*** WARNING: RIGHTS NOT SUFFICIENT ***");
            return;
        }

        currentState = currentState.allScansDone();
        System.out.println("Performing Maintenance");
        System.out.println("Transmitting data...");
        System.out.println("Checking for errors...");
        System.out.println("No errors detected.");
        System.out.println("Maintenance complete!");
        System.out.println("Initiating Shutdown");
    }
}
