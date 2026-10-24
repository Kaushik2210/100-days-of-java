# Day 96: Problem-Solving Patterns — Dynamic Programming

Days 76 to 78 and Day 92 taught how dynamic programming works: overlapping subproblems, a recurrence, and a table (or a cache). The harder skill in practice is **recognizing** that a new problem is a DP problem and quickly choosing the right shape. This day collects the handful of shapes that cover most DP problems you will meet, each with a small worked example.

## A five-question framework

Almost every DP solution answers the same five questions. Answering them *in writing* before coding prevents most mistakes.

1. **State:** what does one table entry mean? ("the best value using the first `i` items", "the answer for the substring `s[i..j]`".) This is the most important question; a vague state gives a vague recurrence.
2. **Recurrence:** how does an entry follow from smaller entries? Usually it is a choice (take or skip, extend or restart) and you pick the best.
3. **Base cases:** which entries are known without computing?
4. **Order:** in what order can entries be filled so each one's inputs are ready? (Or let memoization decide, Day 92.)
5. **Answer:** which entry, or which combination of entries, is the final answer? It is not always the last cell.

Then check for space reductions: if an entry only needs the previous one or two, the table collapses to a few variables.

## Pattern 1: linear choices (take it or skip it)

Process items left to right and decide for each whether to take it, where taking one rules out its neighbor. The classic is **House Robber**: given the amount of money in each house in a row, find the maximum you can take if you may not rob two adjacent houses.

- **State:** `best[i]` = the most money obtainable from the first `i` houses.
- **Recurrence:** either skip house `i` (keep `best[i-1]`), or rob it and add it to the best result that ended *two* houses back: `best[i] = max(best[i-1], best[i-2] + money[i])`.
- **Base cases:** `best[0] = 0`, and `best[1] = money[0]`.
- **Answer:** `best[n]`.

Each entry only reads the two before it, so two variables suffice:

```java
int rob(int[] money) {
    int twoBack = 0;        // best for the first i-2 houses
    int oneBack = 0;        // best for the first i-1 houses
    for (int amount : money) {
        int current = Math.max(oneBack, twoBack + amount);   // skip this house, or rob it
        twoBack = oneBack;
        oneBack = current;
    }
    return oneBack;
}
```

This is O(n) time and O(1) space. The same skeleton, "best of skipping or taking-after-a-gap", underlies a whole family: climbing stairs (Day 92), decoding strings into letters, and maximum non-adjacent sums.

**Recognizing it:** the input is a sequence, you make a decision per element, and the decision only interacts with a *bounded* number of previous elements.

## Pattern 2: best subsequence ending here (Longest Increasing Subsequence)

Find the length of the longest **strictly increasing subsequence** of an array (elements stay in order but may be skipped). Unlike House Robber, an element can extend *any* earlier smaller element, not just a nearby one.

- **State:** `len[i]` = the length of the longest increasing subsequence that **ends exactly at index `i`**.
- **Recurrence:** `len[i] = 1 + max(len[j])` over all earlier `j` with `a[j] < a[i]` (or just `1` if none exist).
- **Base case:** every element alone is a subsequence of length 1.
- **Answer:** the **maximum over all `i`**, not the last entry, since the best subsequence can end anywhere.

That last point is the classic trap: here the answer is not in the final cell.

```java
int lisQuadratic(int[] a) {
    int n = a.length;
    int[] len = new int[n];
    int best = 0;
    for (int i = 0; i < n; i++) {
        len[i] = 1;
        for (int j = 0; j < i; j++) {
            if (a[j] < a[i]) len[i] = Math.max(len[i], len[j] + 1);
        }
        best = Math.max(best, len[i]);
    }
    return best;
}
```

This is **O(n²)**. A well-known refinement gets **O(n log n)** by changing what the state means. Keep an array `tails` where `tails[k]` is the **smallest possible last value** of an increasing subsequence of length `k + 1`. `tails` is always sorted, so for each new value `x` a binary search (Day 53) finds the first tail that is `>= x`. Replace it with `x` (a smaller tail is never worse), or, if every tail is smaller than `x`, append `x` (the longest subsequence just grew).

```java
int lisFast(int[] a) {
    int[] tails = new int[a.length];
    int size = 0;
    for (int x : a) {
        int lo = 0, hi = size;
        while (lo < hi) {                       // lower bound: first position whose tail is >= x
            int mid = (lo + hi) / 2;
            if (tails[mid] < x) lo = mid + 1;
            else hi = mid;
        }
        tails[lo] = x;
        if (lo == size) size++;
    }
    return size;                                 // the number of tails is the LIS length
}
```

Using "first tail `>= x`" (not `> x`) is what makes it *strictly* increasing: an equal value replaces the equal tail instead of extending the subsequence. Note that `tails` itself is **not** necessarily an actual longest subsequence; only its length is meaningful.

**Recognizing it:** "longest/best chain where each element must relate to an earlier chosen one" (increasing, divisible, nestable).

## Pattern 3: reachable totals (Subset Sum and Partition)

Can the numbers in an array be split into two groups with equal sums? That asks whether some subset adds up to exactly **half** of the total (and the total must be even). This is Day 77's knapsack in its simplest, yes/no form.

- **State:** `reachable[s]` = can some subset of the items processed so far sum to exactly `s`?
- **Recurrence:** processing an item `v`, a sum `s` becomes reachable if `s - v` already was: `reachable[s] |= reachable[s - v]`.
- **Base case:** `reachable[0] = true` (the empty subset).
- **Answer:** `reachable[total / 2]`.

```java
boolean canPartition(int[] nums) {
    int total = 0;
    for (int v : nums) total += v;
    if (total % 2 != 0) return false;           // an odd total can never split evenly
    int target = total / 2;

    boolean[] reachable = new boolean[target + 1];
    reachable[0] = true;
    for (int v : nums) {
        for (int s = target; s >= v; s--) {      // downward, so each item is used at most once (as in Day 77's 0/1 knapsack)
            if (reachable[s - v]) reachable[s] = true;
        }
    }
    return reachable[target];
}
```

This is **O(n × target)** time and **O(target)** space, pseudo-polynomial like Day 77. The same loop answers "is this exact total possible?", "how many subsets reach this total?" (change `boolean` to counts), and "closest total at or below a limit."

**Recognizing it:** "exactly this sum / this capacity", "split into groups", "can we make this amount from these items".
