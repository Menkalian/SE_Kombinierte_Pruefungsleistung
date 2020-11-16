package security.components;

import security.staff.Employee;

import java.util.Deque;
import java.util.concurrent.LinkedBlockingDeque;

public class RollerConveyor {
    private final Deque<Tray> trayQueue = new LinkedBlockingDeque<>();
    private final BaggageScanner connectedScanner;
    private Employee workingInspector;

    public RollerConveyor (BaggageScanner connectedScanner) {
        this.connectedScanner = connectedScanner;
    }

    public Employee getWorkingInspector () {
        return workingInspector;
    }

    public void setWorkingInspector (Employee workingInspector) {
        this.workingInspector = workingInspector;
    }

    public Deque<Tray> getTrayQueue () {
        return trayQueue;
    }

    public BaggageScanner getConnectedScanner () {
        return connectedScanner;
    }

    public void pushTrays () {
        connectedScanner.getBelt().queueTray(trayQueue.pollFirst());
    }

    public void addTray (Tray added) {
        trayQueue.addLast(added);
    }
}
