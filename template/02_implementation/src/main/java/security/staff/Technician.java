package security.staff;

import security.components.IBaggageScanner;

public class Technician extends Employee {
    public Technician (String id, String name, String birthDate) {
        this.id = id;
        this.name = name;
        this.birthDate = birthDate;
    }

    public void performMaintenance (IBaggageScanner baggageScanner) {
        System.out.printf("Technician  : \"%s\" performing Maintenance on a Baggage Scanner.%n", getName());
        baggageScanner.maintenance();
    }
}
