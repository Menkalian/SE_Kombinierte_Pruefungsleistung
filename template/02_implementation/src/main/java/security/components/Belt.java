package security.components;

import java.util.Deque;
import java.util.concurrent.LinkedBlockingDeque;

public class Belt {

    private BaggageScanner connectedScanner;
    private Deque<Tray> trayQueue = new LinkedBlockingDeque<>();

    public BaggageScanner getConnectedScanner () {
        return connectedScanner;
    }

    public void setConnectedScanner (BaggageScanner connectedScanner) {
        this.connectedScanner = connectedScanner;
    }

    public Tray moveRight () {
        return null;
    }

    public void moveBackwards (Tray fromScanner) {

    }

    public void queueTray (Tray tray) {

    }
}
