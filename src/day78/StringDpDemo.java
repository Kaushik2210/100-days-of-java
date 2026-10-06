import java.util.Random;

public class StringDpDemo {

    public static void main(String[] args) {
        String a = "ABCBDAB";
        String b = "BDCABA";
        int[][] table = lcsTable(a, b);
        String lcs = lcsString(a, b, table);
        System.out.println("LCS length of \"" + a + "\" and \"" + b + "\" = " + table[a.length()][b.length()]
            + " (known textbook answer: 4)");
        System.out.println("one LCS = \"" + lcs + "\"");
        System.out.println("is a subsequence of both: " + (isSubsequence(lcs, a) && isSubsequence(lcs, b))
            + ", length matches table: " + (lcs.length() == table[a.length()][b.length()]));

        System.out.println();
        Random random = new Random(21); // fixed seed so the run is repeatable
        int trials = 400;
        int lengthMismatches = 0;
        int invalidStrings = 0;
        for (int t = 0; t < trials; t++) {
            String x = randomString(random, random.nextInt(9));
            String y = randomString(random, random.nextInt(9));
            int[][] dp = lcsTable(x, y);
            int dpLength = dp[x.length()][y.length()];
            if (dpLength != bruteForceLcsLength(x, y)) lengthMismatches++;

            String recovered = lcsString(x, y, dp);
            if (recovered.length() != dpLength || !isSubsequence(recovered, x) || !isSubsequence(recovered, y)) {
                invalidStrings++;
            }
        }
        System.out.println("LCS length vs exhaustive search over " + trials + " random pairs: "
            + lengthMismatches + " mismatches");
        System.out.println("recovered LCS strings that were invalid: " + invalidStrings);
    }

    static int[][] lcsTable(String a, String b) {
        int[][] dp = new int[a.length() + 1][b.length() + 1];

        for (int i = 1; i <= a.length(); i++) {
            for (int j = 1; j <= b.length(); j++) {
                if (a.charAt(i - 1) == b.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1] + 1;
                } else {
                    dp[i][j] = Math.max(dp[i - 1][j], dp[i][j - 1]);
                }
            }
        }
        return dp;
    }

    static String lcsString(String a, String b, int[][] dp) {
        StringBuilder result = new StringBuilder();
        int i = a.length();
        int j = b.length();
        while (i > 0 && j > 0) {
            if (a.charAt(i - 1) == b.charAt(j - 1)) {
                result.append(a.charAt(i - 1));
                i--;
                j--;
            } else if (dp[i - 1][j] >= dp[i][j - 1]) {
                i--;
            } else {
                j--;
            }
        }
        return result.reverse().toString();
    }

    static boolean isSubsequence(String small, String big) {
        int k = 0;
        for (int i = 0; i < big.length() && k < small.length(); i++) {
            if (big.charAt(i) == small.charAt(k)) k++;
        }
        return k == small.length();
    }

    // tries every subsequence of x (2^|x| of them) and keeps the longest that is also a subsequence of y
    static int bruteForceLcsLength(String x, String y) {
        int best = 0;
        for (int mask = 0; mask < (1 << x.length()); mask++) {
            StringBuilder candidate = new StringBuilder();
            for (int i = 0; i < x.length(); i++) {
                if ((mask & (1 << i)) != 0) candidate.append(x.charAt(i));
            }
            if (candidate.length() > best && isSubsequence(candidate.toString(), y)) {
                best = candidate.length();
            }
        }
        return best;
    }

    static String randomString(Random random, int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append((char) ('a' + random.nextInt(3))); // small alphabet so common subsequences are likely
        }
        return sb.toString();
    }
}
