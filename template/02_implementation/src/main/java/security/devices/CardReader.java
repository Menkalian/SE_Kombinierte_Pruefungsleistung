package security.devices;

import security.algorithm.AES;
import security.components.OperatingStation;
import security.data.IDCard;

public class CardReader {
    private final AES encrypter;
    private IDCard lastScanned;
    private int wrongInputs;
    private OperatingStation connectedOperatingStation;

    public CardReader (AES encrypter, OperatingStation connectedOperatingStation) {
        this.encrypter = encrypter;
        this.connectedOperatingStation = connectedOperatingStation;
    }

    public void swipeCard (IDCard card) {

    }

    public void enterPin (String pin) {

    }

    public OperatingStation getConnectedOperatingStation () {
        return connectedOperatingStation;
    }

    public void setConnectedOperatingStation (OperatingStation connectedOperatingStation) {
        this.connectedOperatingStation = connectedOperatingStation;
    }
}
