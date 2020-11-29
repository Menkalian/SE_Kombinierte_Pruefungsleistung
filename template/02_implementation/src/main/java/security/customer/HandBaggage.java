package security.customer;

import security.devices.ExplosivesTestStrip;

import java.util.Arrays;

public class HandBaggage {

    private final Passenger owner;
    private Layer[] layers;


    public HandBaggage (Passenger owner, Layer[] layers) {
        this.owner = owner;
        this.layers = layers;
    }


    public ExplosivesTestStrip swipeTest () {
        return new ExplosivesTestStrip();
    }

    public String takeContent (int layer, int position, int length) {
        final char[] original = layers[layer].getContent();
        char[] taken = Arrays.copyOfRange(original, position, position + length);
        for (int i = 0 ; i < length ; i++) {
            original[position + i] = ' ';
        }
        layers[layer].setContent(original);
        final String takenString = new String(taken);
        System.out.printf("Hand Baggage: \"%s\" was taken from the baggage of \"%s\". [Layer: %d; Position: %d]%n", takenString, owner.getName(), layer, position);
        return takenString;
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
