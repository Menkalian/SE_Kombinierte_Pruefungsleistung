package security.staff;

import security.data.IDCard;
import security.devices.CardReader;

public abstract class Employee {
    protected String id;
    protected String name;
    protected String birthDate;
    protected IDCard idCard;


    public void enterPIN (CardReader terminal) {
        System.out.println("Employee    : \"" + name + "\" is trying to authenticate at the CardReader");

        // Default value for PIN = Year of birth. Completely safe ;-)
        terminal.swipeCard(idCard);
        terminal.enterPin(birthDate.split("\\.")[2]);
    }


    public IDCard getIdCard () {
        return idCard;
    }

    public void setIdCard (IDCard idCard) {
        this.idCard = idCard;
    }


    public String getId () {
        return id;
    }


    public String getName () {
        return name;
    }


    public String getBirthDate () {
        return birthDate;
    }
}
