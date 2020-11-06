package security.staff;

public class Supervisor extends Employee {
    private final boolean isSenior;
    private final boolean isExecutive;

    public Supervisor (String id, String name, String birthDate, boolean isSenior, boolean isExecutive) {
        this.id = id;
        this.name = name;
        this.birthDate = birthDate;
        this.isSenior = isSenior;
        this.isExecutive = isExecutive;
    }

    public boolean isSenior () {
        return isSenior;
    }

    public boolean isExecutive () {
        return isExecutive;
    }
}
