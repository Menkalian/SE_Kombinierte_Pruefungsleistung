package security.components;

import security.customer.HandBaggage;
import security.customer.Passenger;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class Track {
    public final List<Tray> trays = new LinkedList<>();
    private final int trackNumber;
    private final List<Passenger> waitingPassengers = new LinkedList<>();

    public Track (int trackNumber) {
        this.trackNumber = trackNumber;
    }

    public void passengerWaiting (Passenger waiting) {
        System.out.printf("Passenger \"%s\" is now waiting at Track %d.%n", waiting.getName(), trackNumber);
    }

    public void trayArrive (Tray arriving) {
        trays.add(arriving);

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
            System.out.printf("Calling \"%s\" from outgoing track %d to ManualPostControl.%n", called, trackNumber);
            waitingPassengers.remove(called);
        } else {
            System.out.printf("The called Passenger is not waiting at the Outgoing Track %d.%n", trackNumber);
        }
    }

    public void passengerLeavingWithBaggage (Passenger leaving, HandBaggage[] baggage) {
        waitingPassengers.remove(leaving);
        final List<Tray> ownersTrays = trays.stream().filter(tray -> Arrays.stream(baggage).anyMatch(bag -> bag.equals(tray.getContainedBaggage()))).collect(Collectors.toList());
        trays.removeAll(ownersTrays);
        System.out.printf("Passenger \"%s\" left with his %d pieces of Baggage.%n", leaving.getName(), ownersTrays.size());
    }
}
