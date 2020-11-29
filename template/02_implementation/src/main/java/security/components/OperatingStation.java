package security.components;

import security.devices.CardReader;
import security.staff.Employee;
import security.state.Deactivated;
import security.state.Locked;

public class OperatingStation {
    private final BaggageScanner connectedScanner;
    private String authenticatedUserType;
    private Employee presentUser;
    private CardReader cardReader;
    private Button[] buttons;


    public OperatingStation (BaggageScanner connectedScanner) {
        this.connectedScanner = connectedScanner;
    }


    public BaggageScanner getConnectedScanner () {
        return connectedScanner;
    }


    public String getAuthenticatedUserType () {
        return authenticatedUserType;
    }

    public void setAuthenticatedUserType (String authenticatedUserType) {
        this.authenticatedUserType = authenticatedUserType;

        // trigger necessary state transitions
        if (connectedScanner.getCurrentState() instanceof Deactivated) {
            System.out.println("Op. Station : Activating Scanner");
            connectedScanner.setCurrentState(connectedScanner.getCurrentState().authenticated());
        }
        if (authenticatedUserType.equals("S") && this.getConnectedScanner().getCurrentState() instanceof Locked) {
            System.out.println("Op. Station : Unlocking Scanner");
            connectedScanner.setCurrentState(connectedScanner.getCurrentState().unlock());
        }
    }


    public Employee getPresentUser () {
        return presentUser;
    }

    public void setPresentUser (Employee presentUser) {
        this.presentUser = presentUser;
    }


    public CardReader getCardReader () {
        return cardReader;
    }

    public void setCardReader (CardReader cardReader) {
        this.cardReader = cardReader;
    }


    public Button[] getButtons () {
        return buttons;
    }

    public void setButtons (Button[] buttons) {
        this.buttons = buttons;
    }
}
