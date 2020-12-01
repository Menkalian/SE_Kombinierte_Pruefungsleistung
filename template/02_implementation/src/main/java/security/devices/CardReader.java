package security.devices;

import security.algorithm.AES;
import security.components.OperatingStation;
import security.data.IDCard;

import java.util.Objects;

public class CardReader {
    private final OperatingStation connectedOperatingStation;
    private final AES encryptor;
    private IDCard lastScanned;
    private int wrongInputs;


    public CardReader (AES encryptor, OperatingStation connectedOperatingStation) {
        this.encryptor = encryptor;
        this.connectedOperatingStation = connectedOperatingStation;
    }


    public void swipeCard (IDCard card) {
        if (card.isLocked()) {
            System.out.println("Card Reader : Card is locked. Please contact your supervisor to get it unlocked");
            System.out.println("Card Reader : CARD REJECTED");

            // Reset Last Scanned to prevent exploits
            lastScanned = null;
            return;
        }

        if (Objects.equals(lastScanned, card)) {
            System.out.println("Card Reader : Card swiped. No new card detected");
        } else {
            System.out.println("Card Reader : Card swiped. New card detected");

            String plainStripe = encryptor.decrypt(card.getMagnetStripe());
            byte permissions = connectedOperatingStation.getConnectedScanner().getPermissions().get(plainStripe.split("\\*\\*\\*")[1]);

            // Bit 7 is enabled for user-roles that have no access.
            if ((permissions & 1 << 7) != 0) {
                System.out.println("Card Reader : This usertype is not authenticated to use the Scanner");
                System.out.println("Card Reader : CARD REJECTED");
                lastScanned = null;
            } else {
                System.out.println("Card Reader : Please enter your personal PIN!");
                wrongInputs = 0;
                lastScanned = card;
            }
        }
    }

    public void enterPin (String pin) {
        if (lastScanned == null) {
            System.out.println("Card Reader : No Card in memory. Input is ignored");
            return;
        }

        String plainStripe = encryptor.decrypt(lastScanned.getMagnetStripe());

        if (pin.equals(plainStripe.split("\\*\\*\\*")[2])) {
            System.out.println("Card Reader : PIN accepted. Updating authenticated User");
            lastScanned = null;
            this.getConnectedOperatingStation().setAuthenticatedUserType(plainStripe.split("\\*\\*\\*")[1]);
        } else {
            if (++wrongInputs >= 3) {
                System.out.println("Card Reader : Too many wrong inputs. Card is now locked");
                lastScanned.setLocked(true);
                lastScanned = null;
            } else {
                System.out.println("Card Reader : Wrong PIN. Please try again. You have " + (3 - wrongInputs) + " try/tries left.");
            }
        }
    }


    public OperatingStation getConnectedOperatingStation () {
        return connectedOperatingStation;
    }

}
