package security.state;

public class Shutdown extends State {
    @Override
    public State start () {
        return new Deactivated();
    }
}
