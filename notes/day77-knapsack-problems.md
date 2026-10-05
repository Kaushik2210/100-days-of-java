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
