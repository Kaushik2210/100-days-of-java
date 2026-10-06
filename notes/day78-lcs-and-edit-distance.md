# Day 78: Dynamic Programming — Longest Common Subsequence & Edit Distance

Days 76 and 77 filled one-dimensional and "items by capacity" tables. Today's two problems fill a table indexed by *positions in two strings*, one of the most common DP shapes. Both compare two sequences, and both have a recurrence that depends on whether the current characters match.

## Longest Common Subsequence (LCS)

A **subsequence** keeps characters in their original order but may skip any of them: `"ACE"` is a subsequence of `"ABCDE"`. (A *substring* has to be contiguous, which is a different problem.) The LCS of two strings is the longest sequence that is a subsequence of both.

Let `dp[i][j]` be the LCS length of the **first `i` characters of `a`** and the **first `j` characters of `b`**:

- If `a[i-1] == b[j-1]`, the last characters match, so they extend whatever was best for the shorter prefixes: `dp[i][j] = dp[i-1][j-1] + 1`.
- Otherwise one of the two last characters can't be part of the answer, so take the better of dropping one: `dp[i][j] = max(dp[i-1][j], dp[i][j-1])`.
- Base case: an empty prefix has LCS `0`, so row 0 and column 0 are all `0`.

```java
int lcsLength(String a, String b) {
    int[][] dp = new int[a.length() + 1][b.length() + 1];

    for (int i = 1; i <= a.length(); i++) {
        for (int j = 1; j <= b.length(); j++) {
            if (a.charAt(i - 1) == b.charAt(j - 1)) {
                dp[i][j] = dp[i - 1][j - 1] + 1;                  // characters match -- extend the diagonal
            } else {
                dp[i][j] = Math.max(dp[i - 1][j], dp[i][j - 1]);  // skip a character from one side
            }
        }
    }
    return dp[a.length()][b.length()];
}
```

This is O(m × n) time and space, where `m` and `n` are the string lengths. Without DP, checking every subsequence of one string against the other would take O(2^m × n), exponential.

## Recovering the actual subsequence

The table gives the *length*. To get a real LCS string, walk backward from `dp[m][n]`: if the characters match, that character is in the LCS and the walk steps diagonally; otherwise it steps toward whichever neighbor (up or left) holds the larger value.

```java
String lcsString(String a, String b, int[][] dp) {
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
    return result.reverse().toString(); // collected back-to-front, so reverse at the end
}
```

When several different subsequences tie for longest, the traceback just picks one, so an LCS is not unique even though its length is.
