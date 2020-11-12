package security.algorithm;

// Implementation taken from provided Library
public class BruteForce implements IStringMatching {
    @Override
    public int search (String text, String pattern) {
        int n = text.length();
        int m = pattern.length();
        int j;

        for (int i = 0 ; i <= (n - m) ; i++) {
            j = 0;

            while ((j < m) && (text.charAt(i + j) == pattern.charAt(j))) {
                j++;
            }

            if (j == m) {
                return i;
            }
        }

        return -1;
    }
}
