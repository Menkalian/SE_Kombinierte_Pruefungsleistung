package security.components;

import security.customer.HandBaggage;
import security.customer.Passenger;

import java.util.ArrayDeque;
import java.util.Deque;

public class TraySupplement {
    private final Deque<Passenger> passengerQueue = new ArrayDeque<>(568);
    private final BaggageScanner connectedScanner;


    public TraySupplement (BaggageScanner connectedScanner) {
        this.connectedScanner = connectedScanner;
    }


    public void nextPassenger () {
        Passenger current = passengerQueue.pollFirst();

        if (current != null) {
            for (HandBaggage handBaggage : current.getBaggage()) {
                Tray temp = getTray();
                temp.putBaggage(handBaggage);
                connectedScanner.getRollerConveyor().addTray(temp);
            }

            System.out.println("TraySupply  : Passenger \"" + current.getName() + "\" has placed their baggage. They are waiting at the outgoing track.");
            connectedScanner.getOutgoingTracks()[1].passengerWaiting(current);
        }
    }


    public Deque<Passenger> getPassengerQueue () {
        return passengerQueue;
    }

    public Tray getTray () {
        System.out.println("TraySupply  : Providing Tray");
        return new Tray();
    }
}
