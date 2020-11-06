package security.algorithm;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Base64;

// Implementation based on given Library/Example
public class AES {
    private SecretKeySpec key;

    public AES (String key) {
        try {
            byte[] tempKey = key.getBytes(StandardCharsets.UTF_8);
            MessageDigest sha = MessageDigest.getInstance("SHA-1");
            tempKey = sha.digest(tempKey);
            tempKey = Arrays.copyOf(tempKey, 16);
            this.key = new SecretKeySpec(tempKey, "AES");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            byte[] backup = new byte[] {0, 0};
            this.key = new SecretKeySpec(backup, "AES");
        }
    }

    public String encrypt (String plain) {
        try {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return Base64.getEncoder().encodeToString(cipher.doFinal(plain.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

    public String decrypt (String encrypted) {
        try {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, key);
            return new String(cipher.doFinal(Base64.getDecoder().decode(encrypted)));
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }
}
