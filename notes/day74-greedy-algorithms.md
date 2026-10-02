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
