package security.customer;

import security.components.BaggageScanner;
import security.components.Tray;

public class Passenger {
    private final String name;
    private HandBaggage[] baggage;
    private boolean isArrested = false;


    public Passenger (String name) {
        this.name = name;
    }


    public void putBaggageToScan (BaggageScanner scanner) {
        for (HandBaggage handBaggage : baggage) {
            final Tray tray = scanner.getTraySupplyer().getTray();
            tray.putBaggage(handBaggage);
            System.out.printf("Passenger   : \"%s\" has taken a tray and put their baggage in it.%n", name);

            scanner.getRollerConveyor().addTray(tray);
            System.out.printf("Passenger   : \"%s\" has put a new tray on the roller conveyor%n", name);
        }
    }

    @Override
    public String toString () {
        return name;
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
}
