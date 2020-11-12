package security.data.enums;

public enum ProhibitedItem {
    KNIFE("kn!fe"),
    WEAPON("glock|7"),
    EXPLOSIVE("exp|os!ve");

    private final String signature;

    ProhibitedItem (String signature) {
        this.signature = signature;
    }

    public String getSignature () {
        return signature;
    }


    @Override
    public String toString () {
        StringBuilder toReturn = new StringBuilder();
        toReturn.append(this.name());
        while (toReturn.length() < 15)
            toReturn.append(' ');
        return toReturn.toString();
    }
}
