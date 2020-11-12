package security.customer;

import org.jetbrains.annotations.NotNull;
import security.devices.ExplosivesTestStrip;

import java.util.Arrays;

public class HandBaggage {
    @NotNull
    private final Passenger owner;
    @NotNull
    private Layer[] layers;

    public HandBaggage (@NotNull Passenger owner, @NotNull Layer[] layers) {
        this.owner = owner;
        this.layers = layers;
    }

    public ExplosivesTestStrip swipeTest () {
        return new ExplosivesTestStrip();
    }

    public String takeContent (int layer, int position, int length) {
        final @NotNull char[] original = layers[layer].getContent();
        char[] toReturn = Arrays.copyOfRange(original, position, position + length);
        for (int i = 0 ; i < length ; i++) {
            original[position + i] = ' ';
        }
        layers[layer].setContent(original);
        System.out.println("Taken " + String.valueOf(toReturn) + " out of the Baggage of passenger \"" + owner.getName() + "\"");
        return String.valueOf(toReturn);
    }

    public Passenger getOwner () {
        return owner;
    }

    public Layer[] getLayers () {
        return layers;
    }

    public void setLayers (Layer[] layers) {
        this.layers = layers;
    }
}
