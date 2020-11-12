package security.components;

public interface IBaggageScanner {
    void moveBeltForward ();

    void moveBeltBackwards ();

    void scan ();

    void alert ();

    void report ();

    void maintenance ();
}
