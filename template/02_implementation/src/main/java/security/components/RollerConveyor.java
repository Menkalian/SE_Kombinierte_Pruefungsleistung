package security.components;

import java.util.Deque;
import java.util.concurrent.LinkedBlockingDeque;

public class RollerConveyor {
    private final Deque<Tray> trayQueue = new LinkedBlockingDeque<>();
    private BaggageScanner connectedScanner;

    public void pushTrays () {
        connectedScanner.getBelt().queueTray(trayQueue.pollFirst());
    }

    public void addTray (Tray added) {
        trayQueue.addLast(added);
    }
}
