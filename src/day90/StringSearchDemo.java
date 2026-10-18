import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

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

        System.out.println();
        int n = 20000;
        int m = 500;
        verifyComparisons = 0;
        rabinKarp(randomString(random, n, 4), randomString(random, m, 4), 256, 1_000_000_007L);
        long randomCost = verifyComparisons;
        verifyComparisons = 0;
        int allAMatches = rabinKarp("a".repeat(n), "a".repeat(m), 256, 1_000_000_007L).size();
        long allACost = verifyComparisons;
        System.out.println("verification cost for n = " + n + ", m = " + m + ":");
        System.out.println("  random text, random pattern: " + randomCost + " characters compared (hash filtered nearly everything)");
        System.out.println("  all 'a' text, all 'a' pattern: " + allACost + " characters compared for " + allAMatches
            + " real matches (= " + allAMatches + " x " + m + ", the O(n x m) worst case)");

        System.out.println();
        int multiTrials = 800;
        int trieMismatches = 0;
        int multiHashMismatches = 0;
        long totalHits = 0;
        for (int t = 0; t < multiTrials; t++) {
            String s = randomString(random, random.nextInt(51), 3);

            Set<String> mixedLengths = new HashSet<>();
            int count = 1 + random.nextInt(5);
            for (int i = 0; i < count; i++) mixedLengths.add(randomString(random, 1 + random.nextInt(5), 3));
            Set<String> expectedMixed = naiveMulti(s, mixedLengths);
            totalHits += expectedMixed.size();
            if (!expectedMixed.equals(trieSearchAll(s, buildTrie(mixedLengths)))) trieMismatches++;

            int fixedLength = 1 + random.nextInt(4);
            Set<String> sameLength = new HashSet<>();
            for (int i = 0; i < count; i++) sameLength.add(randomString(random, fixedLength, 3));
            if (!naiveMulti(s, sameLength).equals(rabinKarpMulti(s, sameLength, 256, 1_000_000_007L))) multiHashMismatches++;
        }
        System.out.println("multi-pattern search vs naive over " + multiTrials + " random cases (" + totalHits
            + " (position, pattern) hits in the mixed-length cases):");
        System.out.println("  trie search mismatches (patterns of different lengths) = " + trieMismatches);
        System.out.println("  multi-pattern Rabin-Karp mismatches (same length) = " + multiHashMismatches);

        System.out.println();
        int patternCount = 5000;
        int textLength = 50000;
        Set<String> manyPatterns = new HashSet<>();
        while (manyPatterns.size() < patternCount) manyPatterns.add(randomString(random, 8, 8));
        String longText = randomString(random, textLength, 8);
        List<String> patternList = new ArrayList<>(manyPatterns);
        for (int i = 0; i < 40; i++) {
            int at = random.nextInt(textLength - 8);
            String planted = patternList.get(random.nextInt(patternList.size()));
            longText = longText.substring(0, at) + planted + longText.substring(at + 8); // make sure real hits exist
        }

        trieSteps = 0;
        TrieNode root = buildTrie(manyPatterns);
        Set<String> trieHits = trieSearchAll(longText, root);
        Set<String> hashHits2 = rabinKarpMulti(longText, manyPatterns, 256, 1_000_000_007L);
        System.out.println(patternCount + " patterns of length 8 in a text of " + textLength + " characters:");
        System.out.println("  trie search found " + trieHits.size() + " hits using " + trieSteps + " trie steps (at most n x L = "
            + (long) textLength * 8 + ")");
        System.out.println("  multi-pattern Rabin-Karp found " + hashHits2.size() + " hits; identical to the trie's: "
            + trieHits.equals(hashHits2));
        System.out.println("  running a separate single-pattern search per pattern would examine at least "
            + (long) patternCount * (textLength - 7) + " starting positions");
    }

    static long trieSteps;

    static class TrieNode {
        java.util.Map<Character, TrieNode> children = new java.util.HashMap<>();
        boolean isEndOfWord;
    }

    static TrieNode buildTrie(Set<String> patterns) {
        TrieNode root = new TrieNode();
        for (String pattern : patterns) {
            TrieNode current = root;
            for (char c : pattern.toCharArray()) {
                current = current.children.computeIfAbsent(c, k -> new TrieNode());
            }
            current.isEndOfWord = true;
        }
        return root;
    }

    static Set<String> trieSearchAll(String text, TrieNode root) {
        Set<String> hits = new HashSet<>();
        for (int start = 0; start < text.length(); start++) {
            TrieNode node = root;
            for (int i = start; i < text.length(); i++) {
                trieSteps++;
                node = node.children.get(text.charAt(i));
                if (node == null) break;
                if (node.isEndOfWord) hits.add(start + ":" + text.substring(start, i + 1));
            }
        }
        return hits;
    }

    // every pattern must have the same length m; one rolling hash serves all of them
    static Set<String> rabinKarpMulti(String t, Set<String> patterns, long base, long mod) {
        Set<String> hits = new HashSet<>();
        if (patterns.isEmpty()) return hits;
        int m = patterns.iterator().next().length();
        int n = t.length();
        if (m > n) return hits;

        java.util.Map<Long, List<String>> byHash = new java.util.HashMap<>();
        for (String pattern : patterns) {
            byHash.computeIfAbsent(freshHash(pattern, 0, m, base, mod), k -> new ArrayList<>()).add(pattern);
        }

        long[] windowHashes = rollingHashes(t, m, base, mod);
        for (int s = 0; s < windowHashes.length; s++) {
            List<String> candidates = byHash.get(windowHashes[s]);
            if (candidates == null) continue;
            for (String candidate : candidates) {
                if (t.startsWith(candidate, s)) hits.add(s + ":" + candidate); // verify, since hashes can collide
            }
        }
        return hits;
    }

    static Set<String> naiveMulti(String t, Set<String> patterns) {
        Set<String> hits = new HashSet<>();
        for (String pattern : patterns) {
            for (int start = 0; start + pattern.length() <= t.length(); start++) {
                if (t.startsWith(pattern, start)) hits.add(start + ":" + pattern);
            }
        }
        return hits;
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
