package security.components;

import security.customer.HandBaggage;
import security.customer.Passenger;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class Track {
    private final LinkedList<Tray> trays = new LinkedList<>();
    private final int trackNumber;
    private final List<Passenger> waitingPassengers = new LinkedList<>();


    public Track (int trackNumber) {
        this.trackNumber = trackNumber;
    }


    public void passengerWaiting (Passenger waiting) {
        System.out.printf("Track %d     : Passenger \"%s\" is now waiting at this Track%n", trackNumber, waiting.getName());
        waitingPassengers.add(waiting);
    }

    public void trayArrive (Tray arriving) {
        System.out.printf("Track %d     : Tray arriving at Track%n", trackNumber);
        trays.add(arriving);

        // Check if Passenger is leaving with their baggage
        List<HandBaggage> presentBaggage = trays.stream().map(Tray::getContainedBaggage).collect(Collectors.toList());
        final Passenger owner = arriving.getContainedBaggage().getOwner();

        if (presentBaggage.containsAll(Arrays.asList(owner.getBaggage()))) {
            if (waitingPassengers.contains(owner)) {
                passengerLeavingWithBaggage(owner, owner.getBaggage());
            }
        }
    }

    public void callPassenger (Passenger called) {
        if (waitingPassengers.contains(called)) {
            System.out.printf("Track %d     : Calling \"%s\" from outgoing track to ManualPostControl.%n", trackNumber, called.getName());
            waitingPassengers.remove(called);
        } else {
            System.out.printf("Track %d     : The called Passenger is not waiting at this Track.%n", trackNumber);
        }
    }

    public void passengerLeavingWithBaggage (Passenger leaving, HandBaggage[] baggage) {
        final List<Tray> traysToRemove = trays
                .stream()
                .filter(tray -> Arrays.stream(baggage).anyMatch(bag -> bag.equals(tray.getContainedBaggage())))
                .collect(Collectors.toList());

        waitingPassengers.remove(leaving);
        trays.removeAll(traysToRemove);

        System.out.printf("Track %d     : Passenger \"%s\" left with his %d pieces of Baggage.%n", trackNumber, leaving.getName(), traysToRemove.size());
    }


    public LinkedList<Tray> getTrays () {
        return trays;
    }
}
