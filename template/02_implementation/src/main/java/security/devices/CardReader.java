package security.devices;

import security.algorithm.AES;
import security.components.OperatingStation;
import security.data.IDCard;
import security.data.enums.TypeOfIDCard;
import security.simulation.Configuration;

import java.time.Instant;
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

    public static void main (String[] args) {
        final AES aes = new AES("geheimtest");
        final CardReader cardReader = new CardReader(aes, null);

        IDCard testcard1 = new IDCard(1, Instant.MAX, aes.encrypt("***I***1111***"), false, TypeOfIDCard.STAFF);
        IDCard testcard2 = new IDCard(2, Instant.MAX, aes.encrypt("***S***1111***"), false, TypeOfIDCard.STAFF);
        IDCard testcard3 = new IDCard(3, Instant.MAX, aes.encrypt("***O***1111***"), false, TypeOfIDCard.STAFF);
        IDCard testcard4 = new IDCard(4, Instant.MAX, aes.encrypt("***T***1111***"), false, TypeOfIDCard.STAFF);
        IDCard testcard5 = new IDCard(5, Instant.MAX, aes.encrypt("***K***1111***"), false, TypeOfIDCard.STAFF);
        IDCard testcard6 = new IDCard(6, Instant.MAX, aes.encrypt("***I***1111***"), false, TypeOfIDCard.STAFF);

        cardReader.swipeCard(testcard1);
        cardReader.enterPin("1111");
    }

    public void swipeCard (IDCard card) {
        if (Objects.equals(lastScanned, card)) {
            System.out.println("Card swiped. No new card detected.");
        } else {
            System.out.println("Card swiped. New card detected.");

            String plainStripe = encrypter.decrypt(card.getMagnetStripe());
            //byte permissions = connectedOperatingStation.getConnectedScanner().getPermissions().get(plainStripe.split("\\*\\*\\*")[1]);
            byte permissions = new Configuration().getPermissions().get(plainStripe.split("\\*\\*\\*")[1]);
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
            //TODO
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
