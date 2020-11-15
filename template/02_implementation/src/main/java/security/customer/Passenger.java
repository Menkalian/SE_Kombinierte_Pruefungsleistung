package security.customer;

import security.components.BaggageScanner;
import security.components.Tray;

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
        for (HandBaggage handBaggage : baggage) {
            final Tray tray = scanner.getTraySupplyment().getTray();
            tray.putBaggage(handBaggage);
            System.out.printf("Passenger \"%s\" has taken a tray and put their baggage in it.%n", name);

            scanner.getRollerConveyor().addTray(tray);
            System.out.printf("Passenger \"%s\" has put a new tray on the roller conveyor%n", name);
        }
    }
}
