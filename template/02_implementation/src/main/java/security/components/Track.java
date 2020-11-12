package security.components;

import security.customer.HandBaggage;
import security.customer.Passenger;

import java.util.LinkedList;
import java.util.List;

public class Track {
    public List<Tray> trays = new LinkedList<>();
    private final int trackNumber;
    private final List<Passenger> waitingPassengers = new LinkedList<>();

    public Track (int trackNumber) {
        this.trackNumber = trackNumber;
    }

    public void passengerWaiting (Passenger waiting) {

    }

    public void trayArrive (Tray arriving) {

    }

    public void callPassenger (Passenger called) {

    }

    public void passengerLeavingWithBaggage (Passenger leaving, HandBaggage[] baggage) {

    }
}
