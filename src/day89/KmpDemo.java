import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class KmpDemo {

    static long naiveComparisons;
    static long kmpComparisons;

    public static void main(String[] args) {
        String pattern = "ababaca";
        System.out.println("lps(\"" + pattern + "\") = " + Arrays.toString(buildLps(pattern)) + " (expected [0, 0, 1, 2, 3, 0, 1])");
        System.out.println("lps(\"aaaa\") = " + Arrays.toString(buildLps("aaaa")) + " (expected [0, 1, 2, 3])");
        System.out.println("lps(\"abcd\") = " + Arrays.toString(buildLps("abcd")) + " (expected [0, 0, 0, 0])");
        System.out.println("lps(\"aabaaab\") = " + Arrays.toString(buildLps("aabaaab")) + " (expected [0, 1, 0, 1, 2, 2, 3])");

        System.out.println();
        System.out.println("search \"ABABCABAB\" in \"ABABDABACDABABCABAB\": " + kmpSearch("ABABDABACDABABCABAB", "ABABCABAB")
            + " (expected [10])");
        System.out.println("search \"aaa\" in \"aaaaaa\" (overlapping): " + kmpSearch("aaaaaa", "aaa") + " (expected [0, 1, 2, 3])");
        System.out.println("search \"abc\" in \"ababab\": " + kmpSearch("ababab", "abc") + " (expected [])");
        System.out.println("pattern longer than text: " + kmpSearch("ab", "abc") + " (expected [])");

        System.out.println();
        Random random = new Random(89); // fixed seed so the run is repeatable
        int trials = 1000;
        int positionMismatches = 0;
        int lpsMismatches = 0;
        int boundViolations = 0;
        int trialsWithMatches = 0;
        int trialsWithOverlap = 0;
        for (int t = 0; t < trials; t++) {
            String text = randomString(random, random.nextInt(41));
            String pat = randomString(random, 1 + random.nextInt(6)); // two-letter alphabet, so matches are common

            List<Integer> expected = naiveSearch(text, pat);
            kmpComparisons = 0;
            List<Integer> actual = kmpSearch(text, pat);
            if (!expected.equals(actual)) positionMismatches++;
            if (!expected.isEmpty()) trialsWithMatches++;
            for (int i = 0; i + 1 < expected.size(); i++) {
                if (expected.get(i + 1) - expected.get(i) < pat.length()) { trialsWithOverlap++; break; }
            }

            int[] lps = buildLps(pat);
            for (int i = 0; i < pat.length(); i++) {
                if (lps[i] != lpsByDefinition(pat, i)) { lpsMismatches++; break; }
            }

            if (kmpComparisons > 2L * text.length()) boundViolations++;
        }
        System.out.println("KMP vs naive search over " + trials + " random binary-alphabet cases ("
            + trialsWithMatches + " had matches, " + trialsWithOverlap + " had overlapping matches):");
        System.out.println("  match position mismatches = " + positionMismatches);
        System.out.println("  LPS arrays that disagree with the definition = " + lpsMismatches);
        System.out.println("  runs that used more than 2n comparisons = " + boundViolations);

        System.out.println();
        int n = 100000;
        String text = "a".repeat(n);
        String hard = "a".repeat(999) + "b";   // almost matches at every position, then fails on the last character
        naiveComparisons = 0;
        kmpComparisons = 0;
        List<Integer> naiveResult = naiveSearch(text, hard);
        List<Integer> kmpResult = kmpSearch(text, hard);
        System.out.println("worst case: text = \"a\" x " + n + ", pattern = \"a\" x 999 + \"b\" (no match):");
        System.out.println("  naive made " + naiveComparisons + " character comparisons");
        System.out.println("  KMP made   " + kmpComparisons + " character comparisons (bound 2n = " + (2 * n) + ")");
        System.out.println("  both found " + naiveResult.size() + " / " + kmpResult.size() + " matches");

        String allA = "a".repeat(999);
        naiveComparisons = 0;
        kmpComparisons = 0;
        List<Integer> naiveMany = naiveSearch(text, allA);
        List<Integer> kmpMany = kmpSearch(text, allA);
        System.out.println("many overlapping matches: pattern = \"a\" x 999 in the same text:");
        System.out.println("  naive made " + naiveComparisons + " comparisons, KMP made " + kmpComparisons);
        System.out.println("  both lists identical: " + naiveMany.equals(kmpMany) + " (" + kmpMany.size() + " matches, expected "
            + (n - 999 + 1) + ")");
    }

    static String randomString(Random random, int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) sb.append((char) ('a' + random.nextInt(2)));
        return sb.toString();
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

    static List<Integer> kmpSearch(String t, String p) {
        int n = t.length();
        int m = p.length();
        int[] lps = buildLps(p);
        List<Integer> matches = new ArrayList<>();

        int i = 0;
        int k = 0;
        while (i < n) {
            kmpComparisons++; // every iteration performs exactly one character comparison
            if (t.charAt(i) == p.charAt(k)) {
                i++;
                k++;
                if (k == m) {
                    matches.add(i - m);
                    k = lps[k - 1];
                }
            } else if (k > 0) {
                k = lps[k - 1];
            } else {
                i++;
            }
        }
        return matches;
    }

    static List<Integer> naiveSearch(String t, String p) {
        int n = t.length();
        int m = p.length();
        List<Integer> matches = new ArrayList<>();
        for (int start = 0; start + m <= n; start++) {
            int j = 0;
            while (j < m) {
                naiveComparisons++;
                if (t.charAt(start + j) != p.charAt(j)) break;
                j++;
            }
            if (j == m) matches.add(start);
        }
        return matches;
    }

    // the definition, applied literally: the longest k < i+1 whose prefix of length k equals the suffix of length k
    static int lpsByDefinition(String p, int i) {
        for (int k = i; k >= 1; k--) {
            if (p.substring(0, k).equals(p.substring(i + 1 - k, i + 1))) return k;
        }
        return 0;
    }
}
