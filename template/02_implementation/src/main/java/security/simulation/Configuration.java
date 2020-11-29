package security.simulation;

import security.algorithm.BoyerMoore;
import security.algorithm.IStringMatching;

import java.util.HashMap;

public class Configuration {
    private final HashMap<String, Byte> permissions;
    private final IStringMatching searchAlgorithm;
    private final String aesKey;


    public Configuration () {
        // Define Permissions
        permissions = new HashMap<>();
        permissions.put("K", (byte) 0b10000000);
        permissions.put("O", (byte) 0b10000000);
        permissions.put("I", (byte) 0b00001111);
        permissions.put("S", (byte) 0b00010000);
        permissions.put("T", (byte) 0b00100000);

        // Define Algorithm
        searchAlgorithm = new BoyerMoore();

        // Define the AES Key
        aesKey = "$e(uR€";
    }


    public HashMap<String, Byte> getPermissions () {
        return permissions;
    }


    public IStringMatching getSearchAlgorithm () {
        return searchAlgorithm;
    }


    public String getAesKey () {
        return aesKey;
    }
}
