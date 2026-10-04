# Day 76: Dynamic Programming — Memoization & Tabulation Basics

Two earlier days left loose ends. Day 52's naive recursive Fibonacci made 177 calls to compute `fibonacci(10)`, recomputing the same smaller values over and over. Day 74's greedy coin change returned 3 coins for `{1, 3, 4}` and amount 6, when the true optimum was 2. **Dynamic programming (DP)** fixes both, and it works for the same underlying reason.

## When DP applies

A problem is a DP candidate when it has two properties:

- **Overlapping subproblems** — the same smaller subproblem is needed again and again by different branches of the recursion (Day 52's recursion tree showed `fibonacci(3)` computed from scratch many times).
- **Optimal substructure** — the best answer to the whole problem can be assembled from the best answers to its subproblems.

When both hold, solving each distinct subproblem *exactly once* and reusing the stored answer collapses an exponential recursion into a polynomial one.

## Memoization: top-down, cache as you go

Memoization keeps the recursive structure exactly as written, but adds a cache (Day 25's `HashMap`, or just an array) checked before doing any work. The first call for a given input computes and stores the answer; every later call for that input returns the stored value immediately.

```java
long fibMemo(int n, long[] cache) {
    if (n <= 1) return n;
    if (cache[n] != 0) return cache[n];       // already computed -- reuse it, no recursion needed

    cache[n] = fibMemo(n - 1, cache) + fibMemo(n - 2, cache);
    return cache[n];
}
```

Each value from `0` to `n` is now computed exactly once, so the whole thing is O(n) instead of Day 52's O(2ⁿ). The recursion tree that used to branch exponentially now collapses: after `fibMemo(n - 1)` finishes, `fibMemo(n - 2)` is already sitting in the cache.

One caveat: this version uses `0` as the "not computed yet" marker, which is only safe because Fibonacci values for `n >= 2` are never zero. A problem whose real answers can be zero needs a separate `boolean[]` (or `null` markers in an `Integer[]`).

## Tabulation: bottom-up, fill a table in order

Tabulation skips recursion entirely. It figures out the order in which subproblems depend on each other and fills a table iteratively, smallest first, so each entry's dependencies are always already filled in by the time it's needed.

```java
long fibTable(int n) {
    if (n <= 1) return n;
    long[] table = new long[n + 1];
    table[0] = 0;
    table[1] = 1;
    for (int i = 2; i <= n; i++) {
        table[i] = table[i - 1] + table[i - 2]; // each entry depends only on entries already filled
    }
    return table[n];
}
```

Same O(n) time, but no recursion — so no call-stack depth to worry about (Day 42's `StackOverflowError`), and no per-call overhead. When only the last couple of table entries are ever needed, as here, the table can shrink to two variables for O(1) space.

## Memoization vs tabulation

- **Memoization** — easiest to write, since it's the plain recursive solution plus a cache. It also only computes subproblems that are actually reachable, which can matter when many table entries would never be needed.
- **Tabulation** — no recursion overhead or stack-depth risk, and it makes the time and space cost obvious from the loop structure. It requires working out the dependency order up front.

Both give the same answer with the same asymptotic time; the choice is mostly about which is easier to get right for the problem at hand.

## Closing Day 74's loose end: coin change with DP

Greedy failed on `{1, 3, 4}` for amount 6 because committing to the biggest coin first locked out the better answer. DP never commits early. It defines `minCoins[a]` as the true fewest coins needed to make amount `a`, and fills the table from `0` upward. For each amount, it tries *every* coin and keeps whichever leaves the cheapest remainder:

```java
int minCoins(int[] coins, int amount) {
    int[] table = new int[amount + 1];
    Arrays.fill(table, Integer.MAX_VALUE);
    table[0] = 0; // zero coins make amount 0

    for (int a = 1; a <= amount; a++) {
        for (int coin : coins) {
            if (coin <= a && table[a - coin] != Integer.MAX_VALUE) {
                table[a] = Math.min(table[a], table[a - coin] + 1); // use this coin on top of the best answer for what's left
            }
        }
    }
    return table[amount] == Integer.MAX_VALUE ? -1 : table[amount]; // -1 if the amount can't be made at all
}
```

For `{1, 3, 4}` and amount 6, the table fills as `[0, 1, 2, 1, 1, 2, 2]`. At `a = 6`, the coin `3` gives `table[3] + 1 = 2`, beating the coin `4` path (`table[2] + 1 = 3`). The cheapest option is found because every coin is considered at every amount, not just the largest one.

This has both required properties. Subproblems overlap (the best answer for amount 3 feeds amounts 4, 6, 7, and more), and the structure is optimal (the best answer for `a` is one coin plus the best answer for `a - coin`). The cost is O(amount × number of coins) time and O(amount) space, polynomial where Day 74's brute-force check was exponential.

## The general recipe

Most DP problems follow the same steps: define precisely what each table entry means, write the recurrence relating an entry to smaller ones, set the base cases, then decide the fill order. Days 77 and 78 apply exactly this recipe to the knapsack problem and to longest common subsequence and edit distance.
