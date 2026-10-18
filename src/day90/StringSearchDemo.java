import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class StringSearchDemo {

    static long hashHits;          // windows whose hash equaled the pattern's hash
    static long falseHits;         // of those, the ones that were collisions rather than real matches
    static long verifyComparisons; // characters compared while verifying hash hits

    public static void main(String[] args) {
        String text = "ABABDABACDABABCABAB";
        String pattern = "ABABCABAB";
        System.out.println("Rabin-Karp: \"" + pattern + "\" in \"" + text + "\" -> " + rabinKarp(text, pattern, 256, 1_000_000_007L)
            + " (expected [10])");

        System.out.println();
        Random random = new Random(90); // fixed seed so the run is repeatable
        int rollingMismatches = 0;
        int windowsChecked = 0;
        for (int t = 0; t < 200; t++) {
            String s = randomString(random, 5 + random.nextInt(30), 4);
            int m = 1 + random.nextInt(Math.min(6, s.length()));
            long[] rolled = rollingHashes(s, m, 256, 1_000_000_007L);
            for (int start = 0; start + m <= s.length(); start++) {
                windowsChecked++;
                if (rolled[start] != freshHash(s, start, m, 256, 1_000_000_007L)) rollingMismatches++;
            }
        }
        System.out.println("rolling hash vs recomputing every window from scratch: " + windowsChecked
            + " windows checked, " + rollingMismatches + " mismatches");

        System.out.println();
        String big = randomString(random, 20000, 4);
        String pat = big.substring(1234, 1234 + 8);
        List<Integer> expected = naiveSearch(big, pat);
        for (long mod : new long[]{1_000_000_007L, 101L, 7L}) {
            hashHits = 0;
            falseHits = 0;
            List<Integer> found = rabinKarp(big, pat, 256, mod);
            System.out.println("modulus " + mod + ": hash hits = " + hashHits + ", of which collisions = " + falseHits
                + ", real matches found = " + found.size() + ", identical to naive: " + found.equals(expected));
        }
        System.out.println("(a tiny modulus causes many collisions, but verification means the answer is still always correct)");
    }

    static String randomString(Random random, int length, int alphabet) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) sb.append((char) ('a' + random.nextInt(alphabet)));
        return sb.toString();
    }

    static List<Integer> rabinKarp(String t, String p, long base, long mod) {
        int n = t.length();
        int m = p.length();
        List<Integer> matches = new ArrayList<>();
        if (m > n) return matches;

        long highPow = 1;
        for (int i = 0; i < m - 1; i++) highPow = highPow * base % mod;

        long patternHash = 0;
        long windowHash = 0;
        for (int i = 0; i < m; i++) {
            patternHash = (patternHash * base + p.charAt(i)) % mod;
            windowHash = (windowHash * base + t.charAt(i)) % mod;
        }

        for (int s = 0; ; s++) {
            if (windowHash == patternHash) {
                hashHits++;
                if (verify(t, s, p)) matches.add(s);
                else falseHits++;
            }
            if (s + m >= n) break;
            windowHash = ((windowHash - t.charAt(s) * highPow % mod + mod) % mod * base + t.charAt(s + m)) % mod;
        }
        return matches;
    }

    static boolean verify(String t, int start, String p) {
        for (int j = 0; j < p.length(); j++) {
            verifyComparisons++;
            if (t.charAt(start + j) != p.charAt(j)) return false;
        }
        return true;
    }

    // hash of every window of length m, computed with the O(1) rolling update
    static long[] rollingHashes(String t, int m, long base, long mod) {
        int windows = t.length() - m + 1;
        long[] hashes = new long[windows];
        long highPow = 1;
        for (int i = 0; i < m - 1; i++) highPow = highPow * base % mod;

        long h = 0;
        for (int i = 0; i < m; i++) h = (h * base + t.charAt(i)) % mod;
        hashes[0] = h;
        for (int s = 1; s < windows; s++) {
            h = ((h - t.charAt(s - 1) * highPow % mod + mod) % mod * base + t.charAt(s + m - 1)) % mod;
            hashes[s] = h;
        }
        return hashes;
    }

    static long freshHash(String t, int start, int m, long base, long mod) {
        long h = 0;
        for (int i = 0; i < m; i++) h = (h * base + t.charAt(start + i)) % mod;
        return h;
    }

    static List<Integer> naiveSearch(String t, String p) {
        List<Integer> matches = new ArrayList<>();
        for (int start = 0; start + p.length() <= t.length(); start++) {
            if (t.startsWith(p, start)) matches.add(start);
        }
        return matches;
    }
}
