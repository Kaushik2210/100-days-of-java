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
