package security.components;

import java.util.Deque;
import java.util.concurrent.LinkedBlockingDeque;

public class Belt {

    private final Deque<Tray> trayQueue = new LinkedBlockingDeque<>();


    public Deque<Tray> getTrayQueue () {
        return trayQueue;
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
