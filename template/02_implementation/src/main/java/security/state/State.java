package security.state;

import java.util.Arrays;

public abstract class State {
    public State () {
        System.out.println("BagScanState: Changing to State " + getClass().getSimpleName());
    }


    public State start () {
        throw new InvalidStateTransitionException();
    }

    public State shutdown () {
        throw new InvalidStateTransitionException();
    }

    public State authenticated () {
        throw new InvalidStateTransitionException();
    }

    public State allScansDone () {
        throw new InvalidStateTransitionException();
    }

    public State lock () {
        throw new InvalidStateTransitionException();
    }

    public State unlock () {
        throw new InvalidStateTransitionException();
    }

    public State scan () {
        throw new InvalidStateTransitionException();
    }

    public State scanDone () {
        throw new InvalidStateTransitionException();
    }


    @Override
    public String toString () {
        char[] toReturn = new char[21]; // 21 is the required length for Report
        final char[] classname = getClass().getSimpleName().toCharArray();

        Arrays.fill(toReturn, ' ');
        for (int i = 0 ; i < classname.length ; i++) {
            toReturn[i] = Character.toUpperCase(classname[i]);
        }

        return String.valueOf(toReturn);
    }
}
