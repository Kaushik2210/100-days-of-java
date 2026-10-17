import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class KmpDemo {

    public static void main(String[] args) {
        String pattern = "ababaca";
        System.out.println("lps(\"" + pattern + "\") = " + Arrays.toString(buildLps(pattern)) + " (expected [0, 0, 1, 2, 3, 0, 1])");
        System.out.println("lps(\"aaaa\") = " + Arrays.toString(buildLps("aaaa")) + " (expected [0, 1, 2, 3])");
        System.out.println("lps(\"abcd\") = " + Arrays.toString(buildLps("abcd")) + " (expected [0, 0, 0, 0])");
        System.out.println("lps(\"aabaaab\") = " + Arrays.toString(buildLps("aabaaab")) + " (expected [0, 1, 0, 1, 2, 2, 3])");
    }

    static int[] buildLps(String p) {
        int m = p.length();
        int[] lps = new int[m];
        int len = 0;
        int i = 1;
        while (i < m) {
            if (p.charAt(i) == p.charAt(len)) {
                len++;
                lps[i] = len;
                i++;
            } else if (len > 0) {
                len = lps[len - 1];
            } else {
                lps[i] = 0;
                i++;
            }
        }
        return lps;
    }
}
