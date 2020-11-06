package security.state;

public class InvalidStateTransitionException extends RuntimeException {
    public InvalidStateTransitionException () {
        super("Tried to call an invalid State transition.");
    }
}
