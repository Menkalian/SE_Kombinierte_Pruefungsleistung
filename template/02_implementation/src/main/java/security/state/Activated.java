package security.state;

public class Activated extends State{
    @Override
    public State allScansDone () {
        return super.allScansDone();
    }

    @Override
    public State lock () {
        return super.lock();
    }

    @Override
    public State scan () {
        return super.scan();
    }
}
