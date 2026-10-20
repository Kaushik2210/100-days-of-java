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
