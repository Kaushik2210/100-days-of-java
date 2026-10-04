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
