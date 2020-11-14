package security.components;

import security.customer.HandBaggage;
import security.customer.Passenger;

import java.util.ArrayDeque;
import java.util.Deque;

public class TraySupplyment {
    private final Deque<Passenger> passengerQueue = new ArrayDeque<>(568);
    private final BaggageScanner connectedScanner;

    public TraySupplyment (BaggageScanner connectedScanner) {
        this.connectedScanner = connectedScanner;
    }

    public Deque<Passenger> getPassengerQueue () {
        return passengerQueue;
    }

    public Tray getTray () {
        return new Tray();
    }

    public void nextPassenger () {
        Passenger current = passengerQueue.getFirst();
        for (HandBaggage handBaggage : current.getBaggage()) {
            Tray temp = getTray();
            temp.putBaggage(handBaggage);
            connectedScanner.getRollerConveyor().addTray(temp);
        }
        connectedScanner.getOutgoingTracks()[1].passengerWaiting(current);
        System.out.println("Passenger \"" + current.getName() + "\" has placed their baggage. They are waiting at the outgoing track.");
    }
}
