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
