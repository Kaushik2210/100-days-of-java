# Day 89: String Matching — the KMP Algorithm

Phase 11 turns from data structures to recognizing common problem patterns. The first is **string matching**: given a long text `T` of length `n` and a pattern `P` of length `m`, find every position where `P` occurs in `T`. Text editors' "find", log searching, and DNA analysis all start here.

## The naive approach and what it wastes

Day 7's `String.indexOf` style search tries each starting position in `T` and compares characters until a mismatch, then starts over at the next position:

```java
for (int start = 0; start + m <= n; start++) {
    int j = 0;
    while (j < m && t.charAt(start + j) == p.charAt(j)) j++;
    if (j == m) matches.add(start);
}
```

That is O(n × m) in the worst case (Day 51). Take `T = "aaaaaaaaaa"` and `P = "aaab"`: at every start, three characters match before the fourth fails, and then the search throws that knowledge away and begins again one step later, re-reading text characters it has already seen.

## The KMP insight: a mismatch still teaches you something

The **Knuth–Morris–Pratt** algorithm never moves backward in the text. When a mismatch happens after `k` matched characters, those `k` characters are known to equal `P[0..k-1]`. Rather than discard that, KMP asks: what is the longest prefix of `P` that is *also* a suffix of the part just matched? The search can resume as if that many characters had already matched, without re-reading any text.

Everything it needs is precomputed from the pattern alone, in the **LPS array** ("longest proper prefix which is also a suffix"). `lps[i]` is the length of the longest proper prefix of `P[0..i]` (shorter than the whole piece) that is also a suffix of `P[0..i]`.

For `P = "ababaca"`:

| i | prefix `P[0..i]` | longest border | `lps[i]` |
|---|---|---|---|
| 0 | a | none | 0 |
| 1 | ab | none | 0 |
| 2 | aba | a | 1 |
| 3 | abab | ab | 2 |
| 4 | ababa | aba | 3 |
| 5 | ababac | none | 0 |
| 6 | ababaca | a | 1 |

So `lps = [0, 0, 1, 2, 3, 0, 1]`.

## Building the LPS array

The array is built with the same "reuse what you already know" idea: to extend a border of length `len`, check whether the next character continues it; if not, fall back to the next-shorter border, `lps[len - 1]`.

```java
int[] buildLps(String p) {
    int m = p.length();
    int[] lps = new int[m];
    int len = 0;                       // length of the current border
    int i = 1;
    while (i < m) {
        if (p.charAt(i) == p.charAt(len)) {
            len++;                     // the border grows by one
            lps[i] = len;
            i++;
        } else if (len > 0) {
            len = lps[len - 1];        // fall back to the next-shorter border, without advancing i
        } else {
            lps[i] = 0;                // no border at all ends here
            i++;
        }
    }
    return lps;
}
```

## The search

Searching uses the identical pattern. Keep a text index `i` and a count `k` of how many pattern characters are currently matched. On a match, advance both. On a mismatch with `k > 0`, **fall back** to `k = lps[k - 1]` and compare again at the *same* text position. On a mismatch with `k == 0`, move `i` forward. When `k` reaches `m`, record a match and fall back by `lps[m - 1]` so that overlapping matches are still found. (This version assumes the pattern is non-empty.)

```java
List<Integer> kmpSearch(String t, String p) {
    int n = t.length();
    int m = p.length();
    int[] lps = buildLps(p);
    List<Integer> matches = new ArrayList<>();

    int i = 0;                             // position in the text -- never moves backward
    int k = 0;                             // how many pattern characters are matched so far
    while (i < n) {
        if (t.charAt(i) == p.charAt(k)) {
            i++;
            k++;
            if (k == m) {
                matches.add(i - m);        // full match ending just before i
                k = lps[k - 1];            // keep the longest border so overlapping matches are found too
            }
        } else if (k > 0) {
            k = lps[k - 1];                // reuse what already matched; do not advance i
        } else {
            i++;                           // nothing matched at all; move on
        }
    }
    return matches;
}
```

## Why it is O(n + m)

Every iteration of the loop does exactly one character comparison and then does one of two things: it **advances `i`** (a match, or a mismatch with `k == 0`), or it **decreases `k`** (a fallback). Count them separately:

- `i` only moves forward and stops at `n`, so there are at most `n` advancing iterations.
- `k` increases by at most 1 per advancing iteration, so it rises at most `n` times in total. Since it can never go below `0`, it can fall back at most as many times as it has risen, so at most `n` fallbacks.

That bounds the loop at `2n` iterations, so searching is **O(n)**. The LPS construction uses the same argument on the pattern and costs **O(m)**. Together: **O(n + m)** time and O(m) extra space, regardless of how nasty the input is. Compared with the naive O(n × m), this removes the worst case entirely.

## When to reach for it

For a single, one-off search in ordinary code, `String.indexOf` is the right call and is perfectly fast on typical text. KMP earns its keep when you need a **guaranteed linear bound**, when you are matching against a stream you can't re-read, or when you search for the same pattern in many texts and can build the LPS array once. It is also the clearest example of a broader technique: precompute from the pattern what a failed comparison tells you, so that no work is ever repeated.
