package security.staff;

import security.data.IDCard;

public abstract class Employee {
    protected String id;
    protected String name;
    protected String birthDate;
    protected IDCard idCard;

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
