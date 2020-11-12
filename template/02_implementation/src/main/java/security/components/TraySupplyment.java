package security.components;

import security.customer.Passenger;

import java.util.ArrayDeque;
import java.util.Deque;

public class TraySupplyment {
    private final Deque<Passenger> passengerQueue = new ArrayDeque<>(568);
    private BaggageScanner connectedScanner;

    public Tray getTray () {
        return new Tray();
    }

    public void nextPassenger () {

    }
}
