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

        System.out.println();
        System.out.println("editDistance(\"kitten\", \"sitting\") = " + editDistance("kitten", "sitting") + " (known: 3)");
        System.out.println("editDistance(\"intention\", \"execution\") = " + editDistance("intention", "execution") + " (known: 5)");
        System.out.println("editDistance(\"\", \"abc\") = " + editDistance("", "abc") + " (3 insertions)");
        System.out.println("editDistance(\"abc\", \"abc\") = " + editDistance("abc", "abc") + " (identical)");

        int editMismatches = 0;
        int relationMismatches = 0;
        int substitutionNeverWorse = 0;
        for (int t = 0; t < trials; t++) {
            String x = randomString(random, random.nextInt(7));
            String y = randomString(random, random.nextInt(7));
            int dist = editDistance(x, y);
            if (dist != bruteForceEditDistance(x, y)) editMismatches++;

            int pairLcsLength = lcsTable(x, y)[x.length()][y.length()];
            int insertDeleteOnly = insertDeleteDistance(x, y);
            if (insertDeleteOnly != x.length() + y.length() - 2 * pairLcsLength) relationMismatches++;
            if (dist <= insertDeleteOnly) substitutionNeverWorse++;
        }
        System.out.println();
        System.out.println("edit distance vs exhaustive recursion over " + trials + " random pairs: "
            + editMismatches + " mismatches");
        System.out.println("insert/delete-only distance == m + n - 2*LCS in all cases: " + (relationMismatches == 0)
            + " (" + relationMismatches + " violations)");
        System.out.println("allowing substitution never made the distance larger: " + (substitutionNeverWorse == trials));
    }

    static int editDistance(String a, String b) {
        int[][] dp = new int[a.length() + 1][b.length() + 1];

        for (int i = 0; i <= a.length(); i++) dp[i][0] = i;
        for (int j = 0; j <= b.length(); j++) dp[0][j] = j;

        for (int i = 1; i <= a.length(); i++) {
            for (int j = 1; j <= b.length(); j++) {
                if (a.charAt(i - 1) == b.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1];
                } else {
                    dp[i][j] = 1 + Math.min(dp[i - 1][j - 1], Math.min(dp[i - 1][j], dp[i][j - 1]));
                }
            }
        }
        return dp[a.length()][b.length()];
    }

    // same table, but with substitution removed -- only insertions and deletions are allowed
    static int insertDeleteDistance(String a, String b) {
        int[][] dp = new int[a.length() + 1][b.length() + 1];

        for (int i = 0; i <= a.length(); i++) dp[i][0] = i;
        for (int j = 0; j <= b.length(); j++) dp[0][j] = j;

        for (int i = 1; i <= a.length(); i++) {
            for (int j = 1; j <= b.length(); j++) {
                if (a.charAt(i - 1) == b.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1];
                } else {
                    dp[i][j] = 1 + Math.min(dp[i - 1][j], dp[i][j - 1]);
                }
            }
        }
        return dp[a.length()][b.length()];
    }

    // plain recursion with no caching, exponential -- only used on tiny strings to cross-check the DP
    static int bruteForceEditDistance(String a, String b) {
        if (a.isEmpty()) return b.length();
        if (b.isEmpty()) return a.length();
        if (a.charAt(0) == b.charAt(0)) {
            return bruteForceEditDistance(a.substring(1), b.substring(1));
        }
        int substitute = bruteForceEditDistance(a.substring(1), b.substring(1));
        int delete = bruteForceEditDistance(a.substring(1), b);
        int insert = bruteForceEditDistance(a, b.substring(1));
        return 1 + Math.min(substitute, Math.min(delete, insert));
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
