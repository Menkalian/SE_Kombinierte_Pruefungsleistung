package security.state;

public class Maintenance extends State {
    @Override
    public State shutdown () {
        return new Shutdown();
    }
}
