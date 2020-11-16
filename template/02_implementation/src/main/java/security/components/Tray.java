package security.components;

import security.customer.HandBaggage;

public class Tray {

    private HandBaggage containedBaggage;

    public HandBaggage getContainedBaggage () {
        return containedBaggage;
    }

    public void putBaggage (HandBaggage baggage) {
        if (containedBaggage != null) {
            throw new RuntimeException("This Tray has no sufficient Capacity for this Baggage");
        }
        System.out.println("Baggage was stored in Tray.");
        containedBaggage = baggage;
    }

    public HandBaggage takeBaggage () {
        if (containedBaggage == null) {
            throw new RuntimeException("This Tray has no Baggage in it");
        }
        HandBaggage temp = containedBaggage;
        containedBaggage = null;
        return temp;
    }
}
