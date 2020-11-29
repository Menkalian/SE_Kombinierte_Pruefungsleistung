package security.components;

import security.customer.HandBaggage;

import java.util.Objects;

public class Tray {

    private HandBaggage containedBaggage;

    public Tray (HandBaggage containedBaggage) {
        this.containedBaggage = containedBaggage;
    }

    public Tray () {
        this(null);
    }

    public void putBaggage (HandBaggage baggage) {
        if (containedBaggage != null) {
            throw new RuntimeException("Baggage Tray: This Tray's capacity is not sufficient for this Baggage");
        }
        System.out.println("Baggage Tray: Baggage was stored in Tray.");
        containedBaggage = baggage;
    }

    public HandBaggage takeBaggage () {
        if (containedBaggage == null) {
            throw new RuntimeException("Baggage Tray: This Tray has no Baggage in it");
        }
        HandBaggage temp = containedBaggage;
        containedBaggage = null;
        return temp;
    }


    public HandBaggage getContainedBaggage () {
        return containedBaggage;
    }

    @Override
    public boolean equals (Object o) {
        if (this == o) return true;
        if (!(o instanceof Tray)) return false;
        Tray tray = (Tray) o;
        return Objects.equals(getContainedBaggage(), tray.getContainedBaggage());
    }
}
