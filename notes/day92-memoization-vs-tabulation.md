# Day 92: Advanced Recursion — Memoized Recursion vs Iterative DP

Days 76 to 78 solved dynamic-programming problems and mostly reached for a table filled by loops. Day 76 introduced both styles, but only on one problem. Today puts the two **evaluation strategies** side by side on the same recurrences so the real trade-offs become clear. The recurrence itself, the "what depends on what" part, is identical either way. Only *how it is evaluated* changes.

## One recurrence, two ways to evaluate it

Take **unique paths through a grid with obstacles**: count the routes from the top-left cell to the bottom-right cell moving only right or down, where cells marked `1` are blocked.

The recurrence for the number of paths into cell `(r, c)`:

- `0` if the cell is blocked or off the grid,
- `1` for the start cell,
- otherwise `paths(r - 1, c) + paths(r, c - 1)` (arrive from above or from the left).

**Top-down (memoized recursion)** writes the recurrence exactly as it reads, and adds a cache so no cell is computed twice.

```java
long pathsMemo(int[][] grid) {
    int rows = grid.length, cols = grid[0].length;
    long[][] memo = new long[rows][cols];
    boolean[][] computed = new boolean[rows][cols];   // a separate flag, since 0 is a legitimate answer
    return solve(grid, rows - 1, cols - 1, memo, computed);
}

private long solve(int[][] grid, int r, int c, long[][] memo, boolean[][] computed) {
    if (r < 0 || c < 0 || grid[r][c] == 1) return 0;  // off the grid or blocked
    if (r == 0 && c == 0) return 1;                   // reached the start
    if (computed[r][c]) return memo[r][c];            // already solved: reuse it
    long result = solve(grid, r - 1, c, memo, computed) + solve(grid, r, c - 1, memo, computed);
    computed[r][c] = true;
    memo[r][c] = result;
    return result;
}
```

The separate `computed` array is deliberate: `0` is a perfectly good answer here (a blocked region has zero paths), so `0` cannot also mean "not computed yet", unlike Day 76's Fibonacci where it could.

**Bottom-up (tabulation)** drops the recursion. It works out an order in which every cell's inputs are already filled in (top-to-bottom, left-to-right) and walks the whole grid.

```java
long pathsTable(int[][] grid) {
    int rows = grid.length, cols = grid[0].length;
    long[][] dp = new long[rows][cols];
    for (int r = 0; r < rows; r++) {
        for (int c = 0; c < cols; c++) {
            if (grid[r][c] == 1) { dp[r][c] = 0; continue; }
            if (r == 0 && c == 0) { dp[r][c] = 1; continue; }
            long fromAbove = r > 0 ? dp[r - 1][c] : 0;
            long fromLeft = c > 0 ? dp[r][c - 1] : 0;
            dp[r][c] = fromAbove + fromLeft;
        }
    }
    return dp[rows - 1][cols - 1];
}
```

Both are **O(rows × cols)** time and space, and both give the same answer. The differences below are about everything *else*.

## Difference 1: memoization only computes what it needs

Tabulation computes **every** cell in the table, whether or not the final answer depends on it. Memoization starts from the answer and only follows the dependencies, so it evaluates just the states that are actually *reachable* from the question being asked.

On a grid heavily sprinkled with obstacles, the cells reachable backward from the goal can be far fewer than `rows × cols`. The effect is dramatic when the state space is huge but the reachable part is tiny. The classic example is the **Bytelandian gold coin** problem: a coin worth `n` can be exchanged for coins worth `n/2`, `n/3`, and `n/4` (rounded down), and you want the most money you can end up with, either by keeping the coin or by exchanging it.

`best(n) = max(n, best(n/2) + best(n/3) + best(n/4))`, with `best(0) = 0`.

For `n = 1,000,000,000`, a table indexed `0..n` would need a billion entries. But the recurrence only ever asks about values of the form `n / (2^a · 3^b)`, a few thousand distinct states, so a memo table keyed by value (a `HashMap`, Day 25) solves it almost instantly.

```java
long best(long n, Map<Long, Long> memo) {
    if (n == 0) return 0;
    Long cached = memo.get(n);
    if (cached != null) return cached;
    long exchanged = best(n / 2, memo) + best(n / 3, memo) + best(n / 4, memo);
    long result = Math.max(n, exchanged);
    memo.put(n, result);
    return result;
}
```

Tabulation can't do this at all, because it has no way to know in advance which of a billion entries matter.

## Difference 2: recursion depth

Memoized recursion is still recursion, so a long dependency chain means a deep call stack (Day 42). For the grid, depth reaches `rows + cols`, but for a problem like "stairs with `n = 1,000,000` steps" the memoized version needs a million nested calls and fails with `StackOverflowError`, which the tabulated loop never will.

```java
long waysMemo(int n, long[] memo) {                 // ways to climb n stairs taking 1 or 2 steps at a time
    if (n <= 1) return 1;
    if (memo[n] != 0) return memo[n];
    return memo[n] = waysMemo(n - 1, memo) + waysMemo(n - 2, memo); // depth grows with n
}

long waysTable(int n) {                              // constant stack depth, whatever n is
    long prev = 1, current = 1;
    for (int i = 2; i <= n; i++) {
        long next = prev + current;
        prev = current;
        current = next;
    }
    return current;
}
```

(The counts grow like Fibonacci numbers and exceed a `long` long before a million steps, wrapping around silently, so for this example only the call depth matters, not the printed value. A real version would take the result modulo some number.)

Raising the stack size with a `Thread` constructor argument is a workaround, but converting to a loop is the robust fix.

## Difference 3: space can be reduced in a table, rarely in a memo

A table fill goes in a known order, so values that will never be read again can be dropped. In the grid recurrence each row depends only on the row above it, so a single row of `cols` cells, updated in place, replaces the whole 2D table:

```java
long pathsRolling(int[][] grid) {
    int rows = grid.length, cols = grid[0].length;
    long[] row = new long[cols];                      // holds the previous row's values until overwritten
    for (int r = 0; r < rows; r++) {
        for (int c = 0; c < cols; c++) {
            if (grid[r][c] == 1) row[c] = 0;
            else if (r == 0 && c == 0) row[c] = 1;
            else if (c > 0) row[c] += row[c - 1];     // row[c] still holds "from above"; add "from the left"
            // when c == 0 and r > 0, row[c] already holds the value from above, which is the whole answer
        }
    }
    return row[cols - 1];
}
```

Space drops from `O(rows × cols)` to `O(cols)`. Day 77's 1D knapsack was the same trick. A memo cache keeps everything, because a recursion has no schedule telling it when a value is finished with.

## Difference 4: constant factors

Loops over primitive arrays are fast and cache-friendly. Recursion pays for a call per state, and a `HashMap` memo (like the coin problem needs) boxes every key and value (Day 28) and hashes on each access. For the same number of states, tabulation is usually noticeably faster (the demo measures it on a four-million-cell grid), which matters when a problem has millions of states.
