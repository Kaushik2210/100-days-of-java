# Day 91: Matrix Algorithms & Traversal Patterns

Day 6 introduced two-dimensional arrays. A surprising number of interview-style and real problems live on a grid: game boards, images, maps, mazes. They share a small set of patterns, and the most useful insight is that **a grid is a graph you don't have to build**: each cell is a vertex, and its edges are simply "the neighbors you can reach by stepping up, down, left, or right."

## Walking to neighbors: direction arrays

Instead of four copy-pasted blocks of code, store the possible moves in two small arrays and loop. The same pattern extends to eight directions by adding the diagonals.

```java
int[] dr = {-1, 1, 0, 0};   // row offsets:    up, down, left, right
int[] dc = {0, 0, -1, 1};   // column offsets

for (int d = 0; d < 4; d++) {
    int nr = r + dr[d];
    int nc = c + dc[d];
    if (nr < 0 || nr >= rows || nc < 0 || nc >= cols) continue; // always bounds-check before touching the cell
    // ... use grid[nr][nc]
}
```

Forgetting the bounds check (and getting an `ArrayIndexOutOfBoundsException`, Day 21) is by far the most common bug in grid code.

## Grid as an implicit graph: counting islands

Given a grid of `0` (water) and `1` (land), count the **islands**: groups of land cells connected horizontally or vertically. This is "count the connected components" (Day 80), solved with Day 70's DFS, except the neighbor lookup is the direction loop instead of an adjacency list.

```java
int countIslands(int[][] original) {
    int rows = original.length;
    int cols = rows == 0 ? 0 : original[0].length;
    int[][] grid = new int[rows][];
    for (int r = 0; r < rows; r++) grid[r] = original[r].clone(); // work on a copy: the search erases what it visits

    int islands = 0;
    for (int r = 0; r < rows; r++) {
        for (int c = 0; c < cols; c++) {
            if (grid[r][c] == 1) {
                islands++;
                sink(grid, r, c);        // erase this whole island so it is never counted twice
            }
        }
    }
    return islands;
}

private void sink(int[][] grid, int r, int c) {
    if (r < 0 || r >= grid.length || c < 0 || c >= grid[0].length || grid[r][c] != 1) return;
    grid[r][c] = 0;                      // mark visited by overwriting it
    sink(grid, r - 1, c);
    sink(grid, r + 1, c);
    sink(grid, r, c - 1);
    sink(grid, r, c + 1);
}
```

Every cell is entered a bounded number of times, so this is **O(rows × cols)**. A very large single island can recurse as deep as the number of cells, which can threaten the call stack (Day 42); an iterative version with an explicit stack or queue avoids that.

## Shortest path through a maze: BFS on the grid

When every step costs the same, BFS (Day 70) finds the shortest route. Here `0` is open floor and `1` is a wall, and the goal is the fewest steps from the top-left to the bottom-right.

```java
int shortestPath(int[][] maze) {
    int rows = maze.length;
    int cols = maze[0].length;
    if (maze[0][0] == 1 || maze[rows - 1][cols - 1] == 1) return -1;

    int[][] dist = new int[rows][cols];
    for (int[] row : dist) Arrays.fill(row, -1);      // -1 means "not reached yet"
    Queue<int[]> queue = new LinkedList<>();
    dist[0][0] = 0;
    queue.add(new int[]{0, 0});

    while (!queue.isEmpty()) {
        int[] cell = queue.poll();
        int r = cell[0], c = cell[1];
        if (r == rows - 1 && c == cols - 1) return dist[r][c];
        for (int d = 0; d < 4; d++) {
            int nr = r + dr[d], nc = c + dc[d];
            if (nr < 0 || nr >= rows || nc < 0 || nc >= cols) continue;
            if (maze[nr][nc] == 1 || dist[nr][nc] != -1) continue;
            dist[nr][nc] = dist[r][c] + 1;
            queue.add(new int[]{nr, nc});
        }
    }
    return -1;                                         // the goal can't be reached
}
```

Because BFS reaches cells in order of distance, the first time it touches the goal is guaranteed to be by a shortest route, and the whole search is **O(rows × cols)**. If steps had different costs, you would swap the queue for Day 81's priority-queue Dijkstra.

## Spiral order: shrinking boundaries

To read a matrix in a clockwise spiral, keep four moving boundaries (`top`, `bottom`, `left`, `right`), walk one edge at a time, and pull that boundary inward after each edge.

```java
List<Integer> spiralOrder(int[][] m) {
    List<Integer> out = new ArrayList<>();
    if (m.length == 0) return out;
    int top = 0, bottom = m.length - 1, left = 0, right = m[0].length - 1;

    while (top <= bottom && left <= right) {
        for (int c = left; c <= right; c++) out.add(m[top][c]);       // left to right along the top
        top++;
        for (int r = top; r <= bottom; r++) out.add(m[r][right]);     // top to bottom down the right side
        right--;
        if (top <= bottom) {                                           // a single remaining row must not be walked twice
            for (int c = right; c >= left; c--) out.add(m[bottom][c]); // right to left along the bottom
            bottom--;
        }
        if (left <= right) {                                           // likewise for a single remaining column
            for (int r = bottom; r >= top; r--) out.add(m[r][left]);   // bottom to top up the left side
            left++;
        }
    }
    return out;
}
```

The two `if` guards are the part that's easy to get wrong. After walking the top row and right column, a leftover single row or column would otherwise be traversed a second time in the opposite direction. Every cell is visited exactly once, so this is **O(rows × cols)**.

## Rotating a square matrix 90° clockwise, in place

Rotating by building a second matrix is easy: the element at `(r, c)` moves to `(c, n - 1 - r)`. Doing it **in place** (O(1) extra space) takes a neat two-step: **transpose** the matrix (swap `m[r][c]` with `m[c][r]`), then **reverse each row**.

```java
void rotateClockwise(int[][] m) {
    int n = m.length;
    for (int r = 0; r < n; r++) {
        for (int c = r + 1; c < n; c++) {          // only the upper triangle: each pair is swapped once
            int temp = m[r][c];
            m[r][c] = m[c][r];
            m[c][r] = temp;
        }
    }
    for (int[] row : m) {                          // then reverse every row
        for (int i = 0, j = n - 1; i < j; i++, j--) {
            int temp = row[i];
            row[i] = row[j];
            row[j] = temp;
        }
    }
}
```

Rotating counterclockwise is the same idea with the row reversal done first, or by reversing columns after the transpose.

## Searching a row- and column-sorted matrix: the staircase

If every row is sorted left to right and every column is sorted top to bottom, you can search without scanning everything. Start at the **top-right** corner. If that value is larger than the target, nothing in its column can match (everything below it is larger still), so move left. If it is smaller than the target, nothing in its row can match (everything to its left is smaller still), so move down. Each step discards a whole row or column.

```java
boolean searchSorted(int[][] m, int target) {
    int r = 0;
    int c = m[0].length - 1;
    while (r < m.length && c >= 0) {
        if (m[r][c] == target) return true;
        if (m[r][c] > target) c--;   // too big: this column is eliminated
        else r++;                    // too small: this row is eliminated
    }
    return false;
}
```

This is Day 71's two-pointer idea in two dimensions, and it takes **O(rows + cols)** instead of O(rows × cols).

## Matrix multiplication, and Fibonacci in O(log n)

The product of an `n × n` matrix `A` and `B` has `C[i][j] = Σ A[i][k] · B[k][j]`. The direct triple loop costs **O(n³)**.

```java
long[][] multiply(long[][] a, long[][] b) {
    int n = a.length;
    long[][] c = new long[n][n];
    for (int i = 0; i < n; i++)
        for (int j = 0; j < n; j++)
            for (int k = 0; k < n; k++)
                c[i][j] += a[i][k] * b[k][j];
    return c;
}
```

Multiplication is associative, so a matrix can be raised to a power with the same halving trick as Day 73's `fastPower`: square the base, and fold it into the result wherever the exponent has a 1 bit. That turns computing `Mⁿ` into **O(log n)** multiplications.

This gives a startling new way to compute Fibonacci numbers. The pair `(F(n+1), F(n))` is produced from `(F(n), F(n-1))` by multiplying with the matrix `[[1, 1], [1, 0]]`, so

`[[1, 1], [1, 0]]ⁿ = [[F(n+1), F(n)], [F(n), F(n-1)]]`

and `F(n)` is read off the matrix power in O(log n) steps, versus Day 76's O(n) table and Day 52's O(2ⁿ) recursion.

```java
long fibByMatrix(int n) {
    long[][] result = {{1, 0}, {0, 1}};          // identity matrix
    long[][] base = {{1, 1}, {1, 0}};
    int e = n;
    while (e > 0) {
        if ((e & 1) == 1) result = multiply(result, base);
        e >>= 1;
        if (e > 0) base = multiply(base, base);  // skip the final squaring: it can overflow a long for no reason
    }
    return result[0][1];
}
```

The guard skips a pointless final squaring: after the last needed bit, squaring the base once more produces a power of the matrix that the answer never uses. With plain `long` arithmetic, any overflow in that unused value wraps around silently and the answer is still correct, so the guard saves one multiplication rather than fixing a bug. It starts to matter as soon as overflow is not silent. With `Math.multiplyExact` (which throws `ArithmeticException` instead of wrapping) the unguarded version fails on inputs where `F(n)` itself fits comfortably, and with `BigInteger` it wastes work on an enormous throwaway number.
