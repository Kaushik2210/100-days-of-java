import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;

public class MatrixDemo {

    static final int[] dr = {-1, 1, 0, 0};
    static final int[] dc = {0, 0, -1, 1};

    public static void main(String[] args) {
        int[][] islandGrid = {
            {1, 1, 0, 0, 0},
            {1, 1, 0, 0, 0},
            {0, 0, 1, 0, 0},
            {0, 0, 0, 1, 1}
        };
        System.out.println("islands = " + countIslands(islandGrid)
            + " (expected 3: the 2x2 block, the lone cell, and the right-hand pair; diagonal neighbors don't connect)");
        System.out.println("original grid left untouched by the search: " + Arrays.deepToString(islandGrid));

        System.out.println();
        int[][] maze = {
            {0, 0, 0, 0},
            {1, 1, 0, 1},
            {0, 0, 0, 0},
            {0, 1, 1, 0}
        };
        System.out.println("shortest path through the maze = " + shortestPath(maze) + " steps (expected 6)");
        int[][] blocked = {
            {0, 1},
            {1, 0}
        };
        System.out.println("shortest path through a fully walled-off maze = " + shortestPath(blocked) + " (expected -1)");

        System.out.println();
        int[][] sample = {{1, 2, 3, 4}, {5, 6, 7, 8}, {9, 10, 11, 12}};
        System.out.println("spiral of 3x4 = " + spiralOrder(sample) + " (expected [1, 2, 3, 4, 8, 12, 11, 10, 9, 5, 6, 7])");

        int[][] square = {{1, 2, 3}, {4, 5, 6}, {7, 8, 9}};
        rotateClockwise(square);
        System.out.println("3x3 rotated clockwise = " + Arrays.deepToString(square) + " (expected [[7, 4, 1], [8, 5, 2], [9, 6, 3]])");

        int[][] sorted = {{1, 4, 7, 11}, {2, 5, 8, 12}, {3, 6, 9, 16}, {10, 13, 14, 17}};
        System.out.println("staircase search for 8: " + searchSorted(sorted, 8) + ", for 15: " + searchSorted(sorted, 15)
            + " (expected true, false)");

        long[][] product = multiply(new long[][]{{1, 2}, {3, 4}}, new long[][]{{5, 6}, {7, 8}});
        System.out.println("[[1,2],[3,4]] x [[5,6],[7,8]] = " + Arrays.deepToString(product) + " (expected [[19, 22], [43, 50]])");
        System.out.println("fibByMatrix(10) = " + fibByMatrix(10) + ", fibByMatrix(90) = " + fibByMatrix(90)
            + " (expected 55 and 2880067194370816120, the value Day 76 found)");

        System.out.println();
        Random random = new Random(91); // fixed seed so the run is repeatable
        int trials = 500;

        int spiralMismatches = 0;
        int spiralNotPermutation = 0;
        for (int t = 0; t < trials; t++) {
            int rows = 1 + random.nextInt(6);
            int cols = 1 + random.nextInt(6);
            int[][] m = new int[rows][cols];
            for (int r = 0; r < rows; r++)
                for (int c = 0; c < cols; c++) m[r][c] = r * 10 + c; // every value distinct
            List<Integer> byBoundaries = spiralOrder(m);
            if (!byBoundaries.equals(spiralBySimulation(m))) spiralMismatches++;
            List<Integer> sortedValues = new ArrayList<>(byBoundaries);
            java.util.Collections.sort(sortedValues);
            List<Integer> expectedValues = new ArrayList<>();
            for (int[] row : m) for (int v : row) expectedValues.add(v);
            java.util.Collections.sort(expectedValues);
            if (!sortedValues.equals(expectedValues)) spiralNotPermutation++;
        }
        System.out.println("spiral order (boundaries) vs spiral by visited-cell simulation over " + trials + " random shapes incl. 1-row and 1-column: "
            + spiralMismatches + " mismatches, " + spiralNotPermutation + " results that skipped or repeated a cell");

        int rotateMismatches = 0;
        int fourTurnsMismatches = 0;
        for (int t = 0; t < trials; t++) {
            int n = 1 + random.nextInt(6);
            int[][] m = new int[n][n];
            for (int r = 0; r < n; r++)
                for (int c = 0; c < n; c++) m[r][c] = random.nextInt(100);
            int[][] original = deepCopy(m);

            int[][] expected = new int[n][n];
            for (int r = 0; r < n; r++)
                for (int c = 0; c < n; c++) expected[c][n - 1 - r] = m[r][c];
            int[][] turned = deepCopy(m);
            rotateClockwise(turned);
            if (!Arrays.deepEquals(turned, expected)) rotateMismatches++;

            for (int i = 0; i < 4; i++) rotateClockwise(m);
            if (!Arrays.deepEquals(m, original)) fourTurnsMismatches++;
        }
        System.out.println("in-place rotation vs the (r, c) -> (c, n-1-r) formula over " + trials + " matrices: " + rotateMismatches
            + " mismatches; four rotations restored the original in all but " + fourTurnsMismatches + " cases");

        int searchMismatches = 0;
        int searchesRun = 0;
        for (int t = 0; t < trials; t++) {
            int rows = 1 + random.nextInt(6);
            int cols = 1 + random.nextInt(6);
            int[][] m = new int[rows][cols];
            for (int r = 0; r < rows; r++) {
                for (int c = 0; c < cols; c++) {
                    int up = r > 0 ? m[r - 1][c] : 0;
                    int left = c > 0 ? m[r][c - 1] : 0;
                    m[r][c] = Math.max(up, left) + random.nextInt(4); // rows and columns both nondecreasing, with ties
                }
            }
            int max = m[rows - 1][cols - 1];
            for (int target = -1; target <= max + 1; target++) {
                searchesRun++;
                if (searchSorted(m, target) != containsBruteForce(m, target)) searchMismatches++;
            }
        }
        System.out.println("staircase search vs scanning every cell over " + searchesRun + " (matrix, target) pairs: "
            + searchMismatches + " mismatches");

        int islandMismatches = 0;
        int multiIslandGrids = 0;
        int mazeMismatches = 0;
        int reachableMazes = 0;
        for (int t = 0; t < trials; t++) {
            int rows = 1 + random.nextInt(7);
            int cols = 1 + random.nextInt(7);
            int density = 20 + random.nextInt(50);
            int[][] grid = new int[rows][cols];
            for (int r = 0; r < rows; r++)
                for (int c = 0; c < cols; c++) grid[r][c] = random.nextInt(100) < density ? 1 : 0;

            int viaDfs = countIslands(grid);
            if (viaDfs != countIslandsBfs(grid) || viaDfs != countIslandsUnionFind(grid)) islandMismatches++;
            if (viaDfs >= 2) multiIslandGrids++;

            int[][] mazeGrid = new int[rows][cols]; // here 1 means wall
            for (int r = 0; r < rows; r++)
                for (int c = 0; c < cols; c++) mazeGrid[r][c] = random.nextInt(100) < 30 ? 1 : 0;
            int viaBfs = shortestPath(mazeGrid);
            if (viaBfs != shortestPathByRelaxation(mazeGrid)) mazeMismatches++;
            if (viaBfs >= 0) reachableMazes++;
        }
        System.out.println("island count: DFS vs BFS vs Union-Find (Day 80) over " + trials + " random grids ("
            + multiIslandGrids + " with 2+ islands): " + islandMismatches + " disagreements");
        System.out.println("maze BFS vs repeated-relaxation oracle over " + trials + " random mazes (" + reachableMazes
            + " solvable): " + mazeMismatches + " mismatches");

        int associativityFailures = 0;
        int identityFailures = 0;
        for (int t = 0; t < 200; t++) {
            int n = 1 + random.nextInt(4);
            long[][] a = randomMatrix(random, n);
            long[][] b = randomMatrix(random, n);
            long[][] c = randomMatrix(random, n);
            if (!Arrays.deepEquals(multiply(multiply(a, b), c), multiply(a, multiply(b, c)))) associativityFailures++;
            if (!Arrays.deepEquals(multiply(a, identity(n)), a) || !Arrays.deepEquals(multiply(identity(n), a), a)) identityFailures++;
        }
        System.out.println("matrix multiplication: associativity failures = " + associativityFailures
            + ", identity-matrix failures = " + identityFailures + " (out of 200 random cases)");

        int fibMismatches = 0;
        for (int n = 0; n <= 90; n++) {
            if (fibByMatrix(n) != fibIterative(n) || fibByMatrixUnguarded(n) != fibIterative(n)) fibMismatches++;
        }
        System.out.println("matrix Fibonacci (guarded and unguarded, plain long) vs the O(n) loop for n = 0..90: " + fibMismatches
            + " mismatches");
        System.out.println("with Math.multiplyExact instead of silent wrapping, n = 90:");
        System.out.println("  guarded squaring   -> " + tryExact(true, 90));
        System.out.println("  unguarded squaring -> " + tryExact(false, 90));
    }

    static String tryExact(boolean guarded, int n) {
        try {
            return String.valueOf(fibByMatrixExact(n, guarded));
        } catch (ArithmeticException e) {
            return "ArithmeticException (" + e.getMessage() + ")";
        }
    }

    static int[][] deepCopy(int[][] m) {
        int[][] copy = new int[m.length][];
        for (int r = 0; r < m.length; r++) copy[r] = m[r].clone();
        return copy;
    }

    static long[][] randomMatrix(Random random, int n) {
        long[][] m = new long[n][n];
        for (int r = 0; r < n; r++)
            for (int c = 0; c < n; c++) m[r][c] = random.nextInt(21) - 10;
        return m;
    }

    static long[][] identity(int n) {
        long[][] m = new long[n][n];
        for (int i = 0; i < n; i++) m[i][i] = 1;
        return m;
    }

    static int countIslands(int[][] original) {
        int rows = original.length;
        int cols = rows == 0 ? 0 : original[0].length;
        int[][] grid = deepCopy(original);

        int islands = 0;
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (grid[r][c] == 1) {
                    islands++;
                    sink(grid, r, c);
                }
            }
        }
        return islands;
    }

    static void sink(int[][] grid, int r, int c) {
        if (r < 0 || r >= grid.length || c < 0 || c >= grid[0].length || grid[r][c] != 1) return;
        grid[r][c] = 0;
        sink(grid, r - 1, c);
        sink(grid, r + 1, c);
        sink(grid, r, c - 1);
        sink(grid, r, c + 1);
    }

    static int countIslandsBfs(int[][] original) {
        int rows = original.length;
        int cols = original[0].length;
        boolean[][] seen = new boolean[rows][cols];
        int islands = 0;
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (original[r][c] != 1 || seen[r][c]) continue;
                islands++;
                Queue<int[]> queue = new LinkedList<>();
                queue.add(new int[]{r, c});
                seen[r][c] = true;
                while (!queue.isEmpty()) {
                    int[] cell = queue.poll();
                    for (int d = 0; d < 4; d++) {
                        int nr = cell[0] + dr[d], nc = cell[1] + dc[d];
                        if (nr < 0 || nr >= rows || nc < 0 || nc >= cols) continue;
                        if (original[nr][nc] == 1 && !seen[nr][nc]) {
                            seen[nr][nc] = true;
                            queue.add(new int[]{nr, nc});
                        }
                    }
                }
            }
        }
        return islands;
    }

    // a third, structurally different answer: merge neighboring land cells with Day 80's Union-Find
    static int countIslandsUnionFind(int[][] grid) {
        int rows = grid.length;
        int cols = grid[0].length;
        int[] parent = new int[rows * cols];
        for (int i = 0; i < parent.length; i++) parent[i] = i;

        int land = 0;
        int merges = 0;
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (grid[r][c] != 1) continue;
                land++;
                if (r > 0 && grid[r - 1][c] == 1 && union(parent, r * cols + c, (r - 1) * cols + c)) merges++;
                if (c > 0 && grid[r][c - 1] == 1 && union(parent, r * cols + c, r * cols + c - 1)) merges++;
            }
        }
        return land - merges; // every successful merge joins two components into one
    }

    static boolean union(int[] parent, int a, int b) {
        int rootA = find(parent, a);
        int rootB = find(parent, b);
        if (rootA == rootB) return false;
        parent[rootA] = rootB;
        return true;
    }

    static int find(int[] parent, int x) {
        while (parent[x] != x) {
            parent[x] = parent[parent[x]];
            x = parent[x];
        }
        return x;
    }

    static int shortestPath(int[][] maze) {
        int rows = maze.length;
        int cols = maze[0].length;
        if (maze[0][0] == 1 || maze[rows - 1][cols - 1] == 1) return -1;

        int[][] dist = new int[rows][cols];
        for (int[] row : dist) Arrays.fill(row, -1);
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
        return -1;
    }

    // independent oracle: keep relaxing "distance = neighbor's distance + 1" until nothing changes (Bellman-Ford style)
    static int shortestPathByRelaxation(int[][] maze) {
        int rows = maze.length;
        int cols = maze[0].length;
        if (maze[0][0] == 1 || maze[rows - 1][cols - 1] == 1) return -1;
        int INF = Integer.MAX_VALUE / 2;
        int[][] dist = new int[rows][cols];
        for (int[] row : dist) Arrays.fill(row, INF);
        dist[0][0] = 0;

        boolean changed = true;
        while (changed) {
            changed = false;
            for (int r = 0; r < rows; r++) {
                for (int c = 0; c < cols; c++) {
                    if (maze[r][c] == 1) continue;
                    for (int d = 0; d < 4; d++) {
                        int nr = r + dr[d], nc = c + dc[d];
                        if (nr < 0 || nr >= rows || nc < 0 || nc >= cols || maze[nr][nc] == 1) continue;
                        if (dist[nr][nc] + 1 < dist[r][c]) {
                            dist[r][c] = dist[nr][nc] + 1;
                            changed = true;
                        }
                    }
                }
            }
        }
        return dist[rows - 1][cols - 1] >= INF ? -1 : dist[rows - 1][cols - 1];
    }

    static List<Integer> spiralOrder(int[][] m) {
        List<Integer> out = new ArrayList<>();
        if (m.length == 0) return out;
        int top = 0, bottom = m.length - 1, left = 0, right = m[0].length - 1;

        while (top <= bottom && left <= right) {
            for (int c = left; c <= right; c++) out.add(m[top][c]);
            top++;
            for (int r = top; r <= bottom; r++) out.add(m[r][right]);
            right--;
            if (top <= bottom) {
                for (int c = right; c >= left; c--) out.add(m[bottom][c]);
                bottom--;
            }
            if (left <= right) {
                for (int r = bottom; r >= top; r--) out.add(m[r][left]);
                left++;
            }
        }
        return out;
    }

    // independent answer: walk one cell at a time, turning right whenever the next cell is off the grid or already visited
    static List<Integer> spiralBySimulation(int[][] m) {
        int rows = m.length;
        int cols = m[0].length;
        boolean[][] visited = new boolean[rows][cols];
        int[] stepR = {0, 1, 0, -1};  // right, down, left, up
        int[] stepC = {1, 0, -1, 0};
        List<Integer> out = new ArrayList<>();
        int r = 0, c = 0, heading = 0;
        for (int i = 0; i < rows * cols; i++) {
            out.add(m[r][c]);
            visited[r][c] = true;
            int nr = r + stepR[heading];
            int nc = c + stepC[heading];
            if (nr < 0 || nr >= rows || nc < 0 || nc >= cols || visited[nr][nc]) {
                heading = (heading + 1) % 4;
                nr = r + stepR[heading];
                nc = c + stepC[heading];
            }
            r = nr;
            c = nc;
        }
        return out;
    }

    static void rotateClockwise(int[][] m) {
        int n = m.length;
        for (int r = 0; r < n; r++) {
            for (int c = r + 1; c < n; c++) {
                int temp = m[r][c];
                m[r][c] = m[c][r];
                m[c][r] = temp;
            }
        }
        for (int[] row : m) {
            for (int i = 0, j = n - 1; i < j; i++, j--) {
                int temp = row[i];
                row[i] = row[j];
                row[j] = temp;
            }
        }
    }

    static boolean searchSorted(int[][] m, int target) {
        int r = 0;
        int c = m[0].length - 1;
        while (r < m.length && c >= 0) {
            if (m[r][c] == target) return true;
            if (m[r][c] > target) c--;
            else r++;
        }
        return false;
    }

    static boolean containsBruteForce(int[][] m, int target) {
        for (int[] row : m)
            for (int v : row)
                if (v == target) return true;
        return false;
    }

    static long[][] multiply(long[][] a, long[][] b) {
        int n = a.length;
        long[][] c = new long[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                for (int k = 0; k < n; k++)
                    c[i][j] += a[i][k] * b[k][j];
        return c;
    }

    static long[][] multiplyExact(long[][] a, long[][] b) {
        int n = a.length;
        long[][] c = new long[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                for (int k = 0; k < n; k++)
                    c[i][j] = Math.addExact(c[i][j], Math.multiplyExact(a[i][k], b[k][j]));
        return c;
    }

    static long fibByMatrix(int n) {
        long[][] result = {{1, 0}, {0, 1}};
        long[][] base = {{1, 1}, {1, 0}};
        int e = n;
        while (e > 0) {
            if ((e & 1) == 1) result = multiply(result, base);
            e >>= 1;
            if (e > 0) base = multiply(base, base);
        }
        return result[0][1];
    }

    static long fibByMatrixUnguarded(int n) {
        long[][] result = {{1, 0}, {0, 1}};
        long[][] base = {{1, 1}, {1, 0}};
        int e = n;
        while (e > 0) {
            if ((e & 1) == 1) result = multiply(result, base);
            e >>= 1;
            base = multiply(base, base); // squares even after the last needed bit
        }
        return result[0][1];
    }

    static long fibByMatrixExact(int n, boolean guarded) {
        long[][] result = {{1, 0}, {0, 1}};
        long[][] base = {{1, 1}, {1, 0}};
        int e = n;
        while (e > 0) {
            if ((e & 1) == 1) result = multiplyExact(result, base);
            e >>= 1;
            if (!guarded || e > 0) base = multiplyExact(base, base);
        }
        return result[0][1];
    }

    static long fibIterative(int n) {
        long a = 0, b = 1;
        for (int i = 0; i < n; i++) {
            long next = a + b;
            a = b;
            b = next;
        }
        return a;
    }
}
