package security.components;

import security.staff.Employee;
import security.state.Shutdown;

public class Supervision {
    private BaggageScanner connectedScanner;
    private Employee supervisor;

    public void pressPowerButton () {
        System.out.println("Supervisor pressed Power-Button");
        if (connectedScanner.getCurrentState() instanceof Shutdown) {
            connectedScanner.setCurrentState(connectedScanner.getCurrentState().start());
        } else {
            connectedScanner.setCurrentState(connectedScanner.getCurrentState().shutdown());
        }
    }
}
