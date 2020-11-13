package security.staff;

import security.components.BaggageScanner;
import security.components.Button;
import security.components.ManualPostControl;

public class Inspector extends Employee {
    private final boolean isSenior;

    public Inspector (String id, String name, String birthDate, boolean isSenior) {
        this.id = id;
        this.name = name;
        this.birthDate = birthDate;
        this.isSenior = isSenior;
    }

    public void pushTray (BaggageScanner scanner) {

    }

    public void pushButton (Button button) {

    }

    public void notifyKnife () {

    }

    public void triggerAlert () {

    }

    public void testBaggageForExplosives (ManualPostControl postControl) {

    }

    public boolean isSenior () {
        return isSenior;
    }
}
