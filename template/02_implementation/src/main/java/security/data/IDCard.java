package security.data;

import security.data.enums.TypeOfIDCard;
import security.staff.Employee;

import java.time.Instant;

public class IDCard {
    private final int id;
    private final Instant validUntil;
    private final String magnetStripe;
    private final TypeOfIDCard type;
    private Employee owner;
    private boolean isLocked;

    public IDCard (int id, Instant validUntil, String magnetStripe, boolean isLocked, TypeOfIDCard type) {
        this.id = id;
        this.validUntil = validUntil;
        this.magnetStripe = magnetStripe;
        this.isLocked = isLocked;
        this.type = type;
    }

    public int getId () {
        return id;
    }

    public Instant getValidUntil () {
        return validUntil;
    }

    public String getMagnetStripe () {
        return magnetStripe;
    }

    public boolean isLocked () {
        return isLocked;
    }

    public void setLocked (boolean locked) {
        isLocked = locked;
    }

    public TypeOfIDCard getType () {
        return type;
    }

    public Employee getOwner () {
        return owner;
    }

    public void setOwner (Employee owner) {
        this.owner = owner;
    }
}
