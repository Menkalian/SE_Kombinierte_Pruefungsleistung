package security.components;

import java.util.Deque;
import java.util.concurrent.LinkedBlockingDeque;

public class RollerConveyor {
    private BaggageScanner connectedScanner;
    private final Deque<Tray> trayQueue = new LinkedBlockingDeque<>();

    public void pushTrays () {

    }

    public void addTray (Tray added) {

    }
}
