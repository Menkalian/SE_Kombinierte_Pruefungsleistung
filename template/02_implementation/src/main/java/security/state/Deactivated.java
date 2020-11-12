package security.state;

public class Deactivated extends State{
    @Override
    public State shutdown () {
        return new Shutdown();
    }

    @Override
    public State authenticated () {
        return new Activated();
    }
}
