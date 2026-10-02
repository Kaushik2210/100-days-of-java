# Day 74: Greedy Algorithms

A greedy algorithm makes the choice that looks best *right now*, at every step, and never reconsiders it — no backtracking, no exploring alternatives. It's the opposite of Day 73's divide and conquer, which fully solves every subproblem before combining. Greedy is fast (usually a single pass) but only gives a correct answer for problems where the locally-best choice is provably always part of some globally-best solution — not every problem has that property, as this day's second half shows.

## Activity selection: scheduling the most non-overlapping intervals

Given a set of activities, each with a start and end time, select the maximum number that don't overlap. The greedy insight: **always pick the activity that finishes earliest** among the remaining options — finishing early leaves the most room for everything after it, so it can never be a worse choice than picking a later-finishing activity instead.

```java
int maxNonOverlapping(int[][] activities) {
    // sort by end time -- the greedy choice is always "whichever finishes soonest"
    Arrays.sort(activities, (a, b) -> a[1] - b[1]); // Day 26's Comparator

    int count = 0;
    int lastEnd = Integer.MIN_VALUE;
    for (int[] activity : activities) {
        int start = activity[0];
        int end = activity[1];
        if (start >= lastEnd) {       // doesn't overlap with the last activity picked
            count++;
            lastEnd = end;
        }
        // overlapping activities are simply skipped -- never reconsidered
    }
    return count;
}
```

This is O(n log n) — dominated by the sort — and never looks back once a choice is made. Proving it's actually optimal (not just fast) takes an "exchange argument": any solution that picks a later-finishing activity instead of the earliest-finishing one can be rearranged to swap in the earliest-finishing one without making the solution any worse, so the greedy choice is always *at least as good* as any alternative.

## Coin change: where greedy works, and where it doesn't

Making change with the fewest coins looks like another natural fit for greedy: always take the largest coin that doesn't overshoot the remaining amount.

```java
int greedyCoinCount(int[] denominations, int amount) {
    int count = 0;
    for (int i = denominations.length - 1; i >= 0; i--) { // assumes denominations sorted ascending
        while (amount >= denominations[i]) {
            amount -= denominations[i];
            count++;
        }
    }
    return amount == 0 ? count : -1; // -1 if leftover amount couldn't be made at all
}
```

For US coin denominations (`{1, 5, 10, 25}`), this genuinely produces the optimal answer every time — 41 cents becomes 25+10+5+1 = 4 coins, and no better combination exists. But the greedy choice isn't optimal for *every* denomination set. With `{1, 3, 4}` and a target of 6: greedy grabs `4` first, leaving `2`, which then needs two `1`s — three coins total (4+1+1). The actual optimal is two `3`s — only two coins (3+3) — which greedy never even considers, because taking the largest coin first locked in a choice that a smarter approach would have avoided.

## Why this matters

Greedy is only correct when the problem has what's called the **greedy choice property**: the locally optimal choice is provably always extendable into a globally optimal solution (exactly what activity selection's exchange argument established). Coin change with `{1, 3, 4}` doesn't have that property — the best *local* choice (take the biggest coin) can lock out the best *global* solution. When that property doesn't hold, the correct tool is **dynamic programming** (Day 76 onward): instead of committing to one choice per step, it systematically considers the actual optimal answer for every smaller subproblem and builds up from there, guaranteeing correctness at the cost of more work than a single greedy pass.
