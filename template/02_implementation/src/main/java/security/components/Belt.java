package security.components;

import java.util.Deque;
import java.util.concurrent.LinkedBlockingDeque;

public class Belt {

    private BaggageScanner connectedScanner;
    private final Deque<Tray> trayQueue = new LinkedBlockingDeque<>();

    public BaggageScanner getConnectedScanner () {
        return connectedScanner;
    }

    public void setConnectedScanner (BaggageScanner connectedScanner) {
        this.connectedScanner = connectedScanner;
    }

    public Tray moveRight () {
        return trayQueue.pollFirst();
    }

    public void moveBackwards (Tray fromScanner) {
        trayQueue.addFirst(fromScanner);
    }

    public void queueTray (Tray tray) {
        trayQueue.addLast(tray);
    }
}
