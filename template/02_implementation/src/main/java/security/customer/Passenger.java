package security.customer;

import security.components.BaggageScanner;

public class Passenger {
    private final String name;
    private boolean isArrested = false;
    private HandBaggage[] baggage;

    public Passenger (String name) {
        this.name = name;
    }

    public String getName () {
        return name;
    }

    public boolean isArrested () {
        return isArrested;
    }

    public void setArrested (boolean arrested) {
        isArrested = arrested;
    }

    public HandBaggage[] getBaggage () {
        return baggage;
    }

    public void setBaggage (HandBaggage[] baggage) {
        this.baggage = baggage;
    }

    public void putBaggageToScan (BaggageScanner scanner) {

    }
}
