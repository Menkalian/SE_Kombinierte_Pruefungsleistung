package security.components;

import security.data.Record;
import security.data.enums.ScanResultType;
import security.staff.Employee;
import security.staff.FederalPoliceOfficer;
import security.state.State;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class BaggageScanner implements IBaggageScanner {
    private final List<Record> scanResults = new LinkedList<>();
    private State currentState;
    private HashMap<String, Byte> permissions;
    private Employee currentFederalPoliceOfficer;
    private TraySupplyment traySupplyment;
    private RollerConveyor rollerConveyor;
    private Belt belt;
    private Scanner scanner;
    private Track[] outgoingTracks;
    private ManualPostControl manualPostControl;
    private OperatingStation operatingStation;
    private Supervision supervision;

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

    public TraySupplyment getTraySupplyment () {
        return traySupplyment;
    }

    public RollerConveyor getRollerConveyor () {
        return rollerConveyor;
    }

    public Belt getBelt () {
        return belt;
    }

    public Scanner getScanner () {
        return scanner;
    }

    public Track[] getOutgoingTracks () {
        return outgoingTracks;
    }

    public ManualPostControl getManualPostControl () {
        return manualPostControl;
    }

    public OperatingStation getOperatingStation () {
        return operatingStation;
    }

    public Supervision getSupervision () {
        return supervision;
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
        Tray temp = outgoingTracks[0].trays.remove(outgoingTracks[0].trays.size() - 1);
        temp = scanner.move(temp);
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

        System.out.println("Starting Scan Procedure.");
        scanResults.add(scanner.scan());
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
        policeOfficer.arrestPassenger(scanner.getCurrentTray().getContainedBaggage().getOwner());
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
        System.out.println();
        scanResults.forEach(System.out::println);
        System.out.println();
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

        System.out.println("Performing Maintenance");
        System.out.println("Transmitting data...");
        System.out.println("Checking for errors...");
        System.out.println("No errors detected.");
        System.out.println("Maintenance complete!");
        System.out.println("Initiating Shutdown");
        currentState = currentState.shutdown();
    }
}
