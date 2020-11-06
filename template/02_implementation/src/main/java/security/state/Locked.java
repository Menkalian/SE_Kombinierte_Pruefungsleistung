package security.state;

public class Locked extends State {
    @Override
    public State unlock () {
        return super.unlock();
    }
}
