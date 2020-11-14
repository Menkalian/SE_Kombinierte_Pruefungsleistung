package security.components;

import security.staff.Employee;
import security.state.Shutdown;

public class Supervision {
    private final BaggageScanner connectedScanner;
    private Employee supervisor;

    public Supervision (BaggageScanner connectedScanner) {
        this.connectedScanner = connectedScanner;
    }

    public Employee getSupervisor () {
        return supervisor;
    }

    public void setSupervisor (Employee supervisor) {
        this.supervisor = supervisor;
    }

    public void pressPowerButton () {
        System.out.println("Supervisor pressed Power-Button");
        if (connectedScanner.getCurrentState() instanceof Shutdown) {
            connectedScanner.setCurrentState(connectedScanner.getCurrentState().start());
        } else {
            connectedScanner.setCurrentState(connectedScanner.getCurrentState().shutdown());
        }
    }
}
