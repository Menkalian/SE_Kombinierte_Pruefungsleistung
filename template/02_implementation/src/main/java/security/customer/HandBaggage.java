package security.customer;

import org.jetbrains.annotations.NotNull;
import security.devices.ExplosivesTestStrip;

public class HandBaggage {
    @NotNull
    private final Passenger owner;
    @NotNull
    private final Layer[] layers;

    public HandBaggage (@NotNull Passenger owner, @NotNull Layer[] layers) {
        this.owner = owner;
        this.layers = layers;
    }

    public ExplosivesTestStrip swipeTest(){
        return null;
    }
    public String takeContent(int layer, int position, int length){
        return null;
    }

    public Passenger getOwner () {
        return owner;
    }

    public Layer[] getLayers () {
        return layers;
    }
}
