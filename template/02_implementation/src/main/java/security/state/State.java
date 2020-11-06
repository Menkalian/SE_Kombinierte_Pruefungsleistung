package security.state;

public abstract class State {
    public State start(){
        throw new InvalidStateTransitionException();
    }

    public State shutdown(){
        throw new InvalidStateTransitionException();
    }

    public State authenticated(){
        throw new InvalidStateTransitionException();
    }

    public State allScansDone(){
        throw new InvalidStateTransitionException();
    }

    public State lock(){
        throw new InvalidStateTransitionException();
    }

    public State unlock(){
        throw new InvalidStateTransitionException();
    }

    public State scan(){
        throw new InvalidStateTransitionException();
    }

    public State scanDone(){
        throw new InvalidStateTransitionException();
    }

}
