# Day 90: String Matching — Rabin-Karp & Trie-Based Search

Day 89's KMP got a guaranteed O(n + m) by studying the *pattern*. Today covers two other ideas. **Rabin-Karp** compares *numbers* instead of characters by hashing every window of the text. A **trie-based search** (building on Day 68) handles the case KMP doesn't: looking for many patterns at once.

## Rabin-Karp: compare hashes, verify only on a hit

Treat a window of `m` characters as a number in some base `B` and reduce it modulo a large prime `M`. Two equal strings always have equal hashes, so if a window's hash differs from the pattern's hash, that window can be skipped with a single integer comparison. If the hashes *are* equal, the strings might match, but they might also be a **collision** (two different strings sharing a hash), so the characters are then compared directly to be sure. The answer is therefore always correct; the hash only decides how often the expensive check runs.

## The rolling hash

Recomputing each window's hash from scratch would cost O(m) per window, no better than the naive search. The trick is to **update** the hash in O(1) as the window slides one character to the right: remove the contribution of the character leaving, shift everything up one place, and add the character entering.

For a window `t[s..s+m-1]`, hash `h = t[s]·B^(m-1) + t[s+1]·B^(m-2) + ... + t[s+m-1]` (mod `M`). Sliding by one:

`h' = (h − t[s]·B^(m-1)) · B + t[s+m]`  (mod `M`)

```java
List<Integer> rabinKarp(String t, String p) {
    final long BASE = 256;
    final long MOD = 1_000_000_007L;          // a large prime keeps accidental collisions rare
    int n = t.length();
    int m = p.length();
    List<Integer> matches = new ArrayList<>();
    if (m > n) return matches;

    long highPow = 1;                          // BASE^(m-1) mod MOD: the weight of the leading character
    for (int i = 0; i < m - 1; i++) highPow = highPow * BASE % MOD;

    long patternHash = 0;
    long windowHash = 0;
    for (int i = 0; i < m; i++) {
        patternHash = (patternHash * BASE + p.charAt(i)) % MOD;
        windowHash = (windowHash * BASE + t.charAt(i)) % MOD;
    }

    for (int s = 0; ; s++) {
        if (windowHash == patternHash && t.regionMatches(s, p, 0, m)) { // verify: equal hashes may be a collision
            matches.add(s);
        }
        if (s + m >= n) break;
        windowHash = ((windowHash - t.charAt(s) * highPow % MOD + MOD) % MOD * BASE + t.charAt(s + m)) % MOD;
    }
    return matches;
}
```

The `+ MOD` before the final `% MOD` keeps the subtraction from going negative, a classic source of bugs, since Java's `%` keeps the sign of the left operand. All the intermediate products stay well inside a `long`.

## Complexity: expected linear, worst case quadratic

Hashing the pattern and the first window costs O(m). Each slide costs O(1). Verification costs O(m), but only when hashes are equal.

- With a large modulus, collisions are rare, so almost every hash hit is a real match. If there are `k` real matches, the total is about **O(n + m + k·m)**. For typical searches that is effectively linear.
- The worst case is **O(n × m)**. It happens when verification runs on nearly every window, either because the text really does contain a match at almost every position (a text of all `a`s against a pattern of all `a`s) or because an adversary picked an input that collides with your base and modulus.

Unlike KMP (Day 89), Rabin-Karp's linear time is therefore a typical-case statement, not a guarantee. Randomizing the base at startup, or using a very large modulus, makes adversarial collisions impractical.

## Where Rabin-Karp shines: many patterns of the same length

Because the check is "is this window's hash among the pattern hashes?", Rabin-Karp can look for **many patterns at once** by storing all their hashes in a hash table (Day 67) and probing it once per window. That makes it a natural fit for plagiarism detection, where thousands of fixed-length fingerprints are compared against a document, and the same rolling-hash idea extends to two-dimensional pattern matching.

## Trie-based search: many patterns of any lengths

When the patterns have **different lengths**, hashing each length separately gets awkward. A trie (Day 68) handles it directly. Insert every pattern into one trie, then try each text position as a starting point and walk down the trie while the text keeps matching:

```java
List<String> searchAll(String text, TrieNode root) {
    List<String> hits = new ArrayList<>();
    for (int start = 0; start < text.length(); start++) {
        TrieNode node = root;
        for (int i = start; i < text.length(); i++) {
            node = node.children.get(text.charAt(i));
            if (node == null) break;                      // no pattern continues this way: stop early
            if (node.isEndOfWord) {
                hits.add(start + ":" + text.substring(start, i + 1));  // a complete pattern ends here
            }
        }
    }
    return hits;
}
```

The inner walk can never go deeper than the longest pattern, so the cost is **O(n × L)** where `L` is the longest pattern length, independent of how *many* patterns there are. Searching for 10,000 words costs the same as searching for one word of equal length, which is the whole benefit of sharing prefixes in a trie. It also reports every pattern that begins at a given position, including patterns that are prefixes of longer patterns (searching for both `he` and `hers` at a position spelling `hers` finds both).

## Aho-Corasick: the best of both

The `O(n × L)` bound still restarts the walk at every text position. The **Aho-Corasick** algorithm removes that by adding KMP-style **failure links** to the trie: when a walk can't continue, a link jumps to the longest suffix of what has matched so far that is also a trie prefix, so the text pointer never moves backward. The result is O(n + total pattern length + number of matches) for any number of patterns, which is why it sits inside tools like `grep -F` and many virus scanners. It is the direct generalization of Day 89's KMP from one pattern to many.

## Choosing between the approaches

- **`String.indexOf`** — a single search in ordinary code.
- **KMP** — one pattern when a worst-case linear guarantee matters, or when text arrives as a stream.
- **Rabin-Karp** — many patterns of the same length, or when hashing the windows is useful for other reasons.
- **Trie / Aho-Corasick** — many patterns of varying lengths.
