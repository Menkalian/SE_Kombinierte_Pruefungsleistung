package security.components;

import security.data.Record;
import security.staff.Employee;
import security.state.State;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class BaggageScanner implements IBaggageScanner {
    private State currentState;
    private List<Record> scanResults = new LinkedList<>();
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

    @Override
    public void moveBeltForward () {

    }

    @Override
    public void moveBeltBackwards () {

    }

    @Override
    public void scan () {

    }

    @Override
    public void alert () {

    }

    @Override
    public void report () {

    }

    @Override
    public void maintenance () {

    }
}
