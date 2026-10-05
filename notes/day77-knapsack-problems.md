# Day 77: Dynamic Programming — Knapsack Problems

Day 76 gave the DP recipe: define what a table entry means, write the recurrence, set base cases, pick a fill order. The **knapsack problem** is the classic second application, and one of the most reused DP shapes: choose items to maximize total value without exceeding a weight limit.

## The 0/1 knapsack problem

You have `n` items, each with a `weight` and a `value`, and a bag that holds at most `capacity` total weight. Each item can be taken **at most once** (hence "0/1": take it or leave it). Maximize the total value of what you take.

## Why greedy fails here too

Day 74's lesson applies again. A tempting greedy rule is "take items in order of best value-per-weight." With these items and a capacity of 7:

| item | weight | value | value/weight |
|------|--------|-------|--------------|
| A | 1 | 1 | 1.00 |
| B | 3 | 4 | 1.33 |
| C | 4 | 5 | 1.25 |
| D | 5 | 7 | 1.40 |

Greedy takes D first (best ratio, weight 5, 2 capacity left), can't fit B or C, then squeezes in A: total value **8**. But taking B and C together (weight 3 + 4 = 7, exactly full) is worth **9**. Locking in the best-ratio item early blocked the better combination, the same failure mode as greedy coin change.

## The DP formulation

Let `dp[i][w]` be the best value achievable using only the **first `i` items** with a bag of capacity **`w`**. For each item there are exactly two choices:

- **Leave it:** the answer is whatever the first `i - 1` items could do with the same capacity, `dp[i - 1][w]`.
- **Take it** (only possible if its weight fits): its value plus the best the first `i - 1` items could do with the *remaining* capacity, `value + dp[i - 1][w - weight]`.

Take whichever is larger. Base case: with zero items, or zero capacity, the best value is `0`.

```java
int knapsack(int[] weights, int[] values, int capacity) {
    int n = weights.length;
    int[][] dp = new int[n + 1][capacity + 1]; // row 0 and column 0 stay 0: the base cases

    for (int i = 1; i <= n; i++) {
        int weight = weights[i - 1];
        int value = values[i - 1];
        for (int w = 0; w <= capacity; w++) {
            dp[i][w] = dp[i - 1][w];                                  // leave item i
            if (weight <= w) {
                dp[i][w] = Math.max(dp[i][w], value + dp[i - 1][w - weight]); // or take it, if it fits
            }
        }
    }
    return dp[n][capacity];
}
```

Every entry is computed exactly once from entries in the previous row, so this runs in O(n × capacity) time and space. That is **pseudo-polynomial**: polynomial in the numeric value of `capacity`, not in the number of bits needed to write it down, so a capacity in the billions makes the table impractically large even for few items.

## Space optimization: one row is enough

Row `i` only ever reads from row `i - 1`, so the full 2D table is wasteful. A single 1D array can be updated in place, but the **iteration direction matters**. Walking capacity from high to low guarantees that when `dp[w - weight]` is read, it still holds the *previous* item's value, not one already overwritten for the current item.

```java
int knapsack1D(int[] weights, int[] values, int capacity) {
    int[] dp = new int[capacity + 1];
    for (int i = 0; i < weights.length; i++) {
        for (int w = capacity; w >= weights[i]; w--) { // downward: each item is used at most once
            dp[w] = Math.max(dp[w], values[i] + dp[w - weights[i]]);
        }
    }
    return dp[capacity];
}
```

This drops space from O(n × capacity) to O(capacity) with the same O(n × capacity) time. Iterating `w` *upward* instead would let the current item be counted again and again, because `dp[w - weight]` would already include it. That is a different problem, covered next.

## Unbounded knapsack: unlimited copies of each item

If every item can be taken **any number of times**, the same 1D array works with the loop direction flipped to ascending, so that an item's own earlier contribution is intentionally visible when it is considered again:

```java
int unboundedKnapsack(int[] weights, int[] values, int capacity) {
    int[] dp = new int[capacity + 1];
    for (int i = 0; i < weights.length; i++) {
        for (int w = weights[i]; w <= capacity; w++) { // upward: dp[w - weight] may already include this item
            dp[w] = Math.max(dp[w], values[i] + dp[w - weights[i]]);
        }
    }
    return dp[capacity];
}
```

This is closely related to Day 76's coin change: coins are items with unlimited supply, and the target amount plays the role of capacity. There are two differences. Coin change *minimizes* the number of items rather than maximizing a value, and it requires the amount to be hit **exactly**, whereas knapsack is happy to leave some capacity unused. The loop structure is the same, though, and seeing that is a good example of DP problems repeating a small number of underlying patterns under different disguises.

## Choosing between the variants

- **0/1** — each item at most once: loop capacity **downward**.
- **Unbounded** — unlimited copies: loop capacity **upward**.
- Either way, the cost is O(n × capacity) time, and the 1D form needs only O(capacity) space.
