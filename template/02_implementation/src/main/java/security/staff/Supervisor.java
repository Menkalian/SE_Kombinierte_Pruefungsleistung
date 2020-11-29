package security.staff;

import security.components.BaggageScanner;

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

    public void unlockScanner (BaggageScanner scanner) {
        System.out.printf("Supervisor  : \"%s\" is unlocking the scanner%n", getName());
        enterPIN(scanner.getOperatingStation().getCardReader());
    }

    public void switchPower (BaggageScanner scanner) {
        System.out.printf("Supervisor  : \"%s\" is pressing the power-button%n", getName());
        scanner.getSupervision().pressPowerButton();
    }


    public boolean isSenior () {
        return isSenior;
    }

    public boolean isExecutive () {
        return isExecutive;
    }

}
