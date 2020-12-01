package security.components;

import security.customer.Passenger;

import java.util.ArrayDeque;
import java.util.Deque;

public class TraySupplyer {
    private final Deque<Passenger> passengerQueue = new ArrayDeque<>(568);
    private final BaggageScanner connectedScanner;


    public TraySupplyer (BaggageScanner connectedScanner) {
        this.connectedScanner = connectedScanner;
    }


    public void nextPassenger () {
        Passenger current = passengerQueue.pollFirst();

        if (current != null) {
            current.putBaggageToScan(connectedScanner);

            System.out.println("TraySupply  : Passenger \"" + current.getName() + "\" has placed their baggage. They are waiting at the outgoing track.");
            connectedScanner.getOutgoingTracks()[1].passengerWaiting(current);
        }
    }


    public Tray getTray () {
        System.out.println("TraySupply  : Providing Tray");
        return new Tray();
    }


    public Deque<Passenger> getPassengerQueue () {
        return passengerQueue;
    }
}
