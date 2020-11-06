package security.state;

public class Deactivated extends State{
    @Override
    public State shutdown () {
        return super.shutdown();
    }

    @Override
    public State authenticated () {
        return super.authenticated();
    }
}
