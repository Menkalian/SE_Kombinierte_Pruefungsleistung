package security.state;

public class InUse extends State {
    @Override
    public State scanDone () {
        return new Activated();
    }
}
