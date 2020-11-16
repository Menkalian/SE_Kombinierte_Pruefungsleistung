package security.state;

public class Activated extends State {
    @Override
    public State allScansDone () {
        return new Maintenance();
    }

    @Override
    public State lock () {
        return new Locked();
    }

    @Override
    public State scan () {
        return new InUse();
    }
}
