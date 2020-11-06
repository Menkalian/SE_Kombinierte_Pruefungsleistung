package security.components;

import security.devices.CardReader;
import security.staff.Employee;

public class OperatingStation {
    private BaggageScanner connectedScanner;
    private String authenticatedUserType;
    private Employee presentUser;
    private CardReader cardReader;
    private Button[] buttons;
}
