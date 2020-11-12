package security.components;

import security.devices.CardReader;
import security.staff.Employee;

public class OperatingStation {
    private BaggageScanner connectedScanner;
    private String authenticatedUserType;
    private Employee presentUser;
    private CardReader cardReader;
    private Button[] buttons;

    public BaggageScanner getConnectedScanner () {
        return connectedScanner;
    }

    public String getAuthenticatedUserType () {
        return authenticatedUserType;
    }

    public void setAuthenticatedUserType (String authenticatedUserType) {
        this.authenticatedUserType = authenticatedUserType;
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

    public Button[] getButtons () {
        return buttons;
    }
}
