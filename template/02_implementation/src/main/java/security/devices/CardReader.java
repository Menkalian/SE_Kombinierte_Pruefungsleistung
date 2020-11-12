package security.devices;

import security.algorithm.AES;
import security.components.OperatingStation;
import security.data.IDCard;

import java.util.Objects;

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
        if (card.isLocked()) {
            System.out.println("Card is locked. Please contact your supervisor to get it unlocked.");
            System.out.println("CARD REJECTED");
            lastScanned = null;
            return;
        }
        if (Objects.equals(lastScanned, card)) {
            System.out.println("Card swiped. No new card detected.");
        } else {
            System.out.println("Card swiped. New card detected.");

            String plainStripe = encrypter.decrypt(card.getMagnetStripe());
            byte permissions = connectedOperatingStation.getConnectedScanner().getPermissions().get(plainStripe.split("\\*\\*\\*")[1]);
            if ((permissions & 1 << 7) != 0) {
                System.out.println("This usertype is not authenticated to use the Scanner.");
                System.out.println("CARD REJECTED");
                lastScanned = null;
            } else {
                System.out.println("Please enter your personal PIN!");
                wrongInputs = 0;
                lastScanned = card;
            }
        }
    }

    public void enterPin (String pin) {
        if (lastScanned == null) {
            System.out.println("No Card in memory. Input is ignored.");
            return;
        }
        String plainStripe = encrypter.decrypt(lastScanned.getMagnetStripe());
        if (pin.equals(plainStripe.split("\\*\\*\\*")[2])) {
            System.out.println("PIN accepted. Updating authenticated User");
            lastScanned = null;
            this.getConnectedOperatingStation().setAuthenticatedUserType(plainStripe.split("\\*\\*\\*")[1]);
        } else {
            if (++wrongInputs >= 3) {
                System.out.println("Too many wrong inputs. Card is being locked.");
                lastScanned.setLocked(true);
                lastScanned = null;
            } else {
                System.out.println("Wrong PIN. Please try again. You have " + (3 - wrongInputs) + " try/tries left.");
            }
        }
    }

    public OperatingStation getConnectedOperatingStation () {
        return connectedOperatingStation;
    }

    public void setConnectedOperatingStation (OperatingStation connectedOperatingStation) {
        this.connectedOperatingStation = connectedOperatingStation;
    }
}
