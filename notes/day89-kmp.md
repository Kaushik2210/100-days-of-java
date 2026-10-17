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
