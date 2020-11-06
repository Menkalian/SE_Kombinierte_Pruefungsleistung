package security.devices;

import java.util.Arrays;
import java.util.Random;

public class ExplosivesTestStrip {
    private final char[][] stripe;

    public ExplosivesTestStrip () {
        stripe = new char[30][10];
        for (char[] chars : stripe) {
            Arrays.fill(chars, 0, chars.length, 'p');
        }

        // Place exp
        Random rng = new Random();
        int row = rng.nextInt(stripe.length);
        int column = rng.nextInt(stripe[row].length - 2);
        stripe[row][column] = 'e';
        stripe[row][column + 1] = 'x';
        stripe[row][column + 2] = 'p'; // Not necessary, since p is already written
    }

    public char[][] getStripe () {
        return stripe;
    }
}
