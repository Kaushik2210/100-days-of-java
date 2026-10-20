import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class MemoVsTabulationDemo {

    public static void main(String[] args) {
        int[][] grid = {
            {0, 0, 0},
            {0, 1, 0},
            {0, 0, 0}
        };
        System.out.println("paths through the 3x3 grid with the center blocked:");
        System.out.println("  memoized recursion = " + pathsMemo(grid) + " (expected 2: around the left or around the right)");
        System.out.println("  tabulation         = " + pathsTable(grid));

        int[][] open = new int[6][6];
        System.out.println("open 6x6 grid: memoized = " + pathsMemo(open) + ", tabulated = " + pathsTable(open)
            + " (expected C(10,5) = 252)");

        int[][] blockedStart = {{1, 0}, {0, 0}};
        System.out.println("blocked start cell: memoized = " + pathsMemo(blockedStart) + ", tabulated = " + pathsTable(blockedStart)
            + " (expected 0)");

        Random random = new Random(92); // fixed seed so the run is repeatable
        int trials = 500;
        int mismatches = 0;
        int nonZero = 0;
        for (int t = 0; t < trials; t++) {
            int rows = 1 + random.nextInt(6);
            int cols = 1 + random.nextInt(6);
            int[][] g = new int[rows][cols];
            for (int r = 0; r < rows; r++)
                for (int c = 0; c < cols; c++) g[r][c] = random.nextInt(100) < 25 ? 1 : 0;

            long expected = countPathsBruteForce(g, 0, 0);
            if (pathsMemo(g) != expected || pathsTable(g) != expected) mismatches++;
            if (expected > 0) nonZero++;
        }
        System.out.println("memoized and tabulated vs enumerating every path over " + trials + " random grids ("
            + nonZero + " had at least one path): " + mismatches + " mismatches");

        int rollingMismatches = 0;
        for (int t = 0; t < trials; t++) {
            int rows = 1 + random.nextInt(8);
            int cols = 1 + random.nextInt(8);
            int[][] g = new int[rows][cols];
            for (int r = 0; r < rows; r++)
                for (int c = 0; c < cols; c++) g[r][c] = random.nextInt(100) < 20 ? 1 : 0;
            if (pathsRolling(g) != pathsTable(g)) rollingMismatches++;
        }
        System.out.println("single-row rolling array vs full table over " + trials + " random grids: "
            + rollingMismatches + " mismatches (space O(cols) instead of O(rows x cols))");

        System.out.println();
        int size = 60;
        for (int blockedPercent : new int[]{45, 25}) {
            int[][] g = new int[size][size];
            for (int r = 0; r < size; r++)
                for (int c = 0; c < size; c++) g[r][c] = random.nextInt(100) < blockedPercent ? 1 : 0;
            g[0][0] = 0;
            g[size - 1][size - 1] = 0;
            evaluatedCells = 0;
            long found = pathsMemoCounted(g);
            System.out.println(size + "x" + size + " grid, about " + blockedPercent + "% blocked: " + found + " paths; memoized recursion evaluated "
                + evaluatedCells + " of " + size * size + " cells (tabulation always fills all " + size * size + ")");
        }
        System.out.println("(with 45% blocked the goal is walled off, so the memoized search dies out almost immediately; with 25% blocked"
            + " it must explore a much larger part of the grid, so the saving shrinks)");

        System.out.println();
        long[] table = new long[200001];
        for (int i = 1; i < table.length; i++) {
            table[i] = Math.max(i, table[i / 2] + table[i / 3] + table[i / 4]);
        }
        Map<Long, Long> shared = new HashMap<>();
        int coinMismatches = 0;
        for (int n = 0; n <= 200000; n++) {
            if (best(n, shared) != table[n]) coinMismatches++;
        }
        System.out.println("Bytelandian coins: best(12) = " + best(12, new HashMap<>()) + " (expected 13), best(2) = "
            + best(2, new HashMap<>()) + " (expected 2)");
        System.out.println("  memoized HashMap vs full table for every n in 0..200000: " + coinMismatches + " mismatches");

        Map<Long, Long> bigMemo = new HashMap<>();
        long bigAnswer = best(1_000_000_000L, bigMemo);
        System.out.println("  best(1,000,000,000) = " + bigAnswer + " using only " + bigMemo.size() + " distinct states");
        System.out.println("  a table for that n would need 1,000,000,001 longs = about "
            + (1_000_000_001L * 8 / 1_000_000_000L) + " GB");

        System.out.println();
        int[] stairSizes = {1000, 5000, 10000, 50000, 200000, 1_000_000};
        System.out.println("stair climbing, memoized recursion vs loop (call depth = n); results depend on this JVM's stack size:");
        for (int n : stairSizes) {
            String memoOutcome;
            try {
                long viaMemo = waysMemo(n, new long[n + 1]);
                memoOutcome = viaMemo == waysTable(n) ? "ok, matches the loop" : "ok but DIFFERENT from the loop";
            } catch (StackOverflowError e) {
                memoOutcome = "StackOverflowError";
            }
            waysTable(n); // the loop has no recursion, so it simply completes for every n
            System.out.println("  n = " + n + ": memoized -> " + memoOutcome + "; loop -> completed");
        }

        System.out.println();
        int big = 2000;
        int[][] open2000 = new int[big][big];
        pathsMemo(open2000);
        pathsTable(open2000);
        pathsRolling(open2000);                        // warm up all three before timing
        long start = System.nanoTime();
        long viaMemo = pathsMemo(open2000);
        long memoMillis = (System.nanoTime() - start) / 1_000_000;
        start = System.nanoTime();
        long viaTable = pathsTable(open2000);
        long tableMillis = (System.nanoTime() - start) / 1_000_000;
        start = System.nanoTime();
        long viaRolling = pathsRolling(open2000);
        long rollingMillis = (System.nanoTime() - start) / 1_000_000;
        System.out.println(big + "x" + big + " open grid (" + (long) big * big + " cells; values wrap around in a long, identically in all three):");
        System.out.println("  memoized recursion: " + memoMillis + " ms");
        System.out.println("  full table:         " + tableMillis + " ms");
        System.out.println("  rolling row:        " + rollingMillis + " ms");
        System.out.println("  all three agree: " + (viaMemo == viaTable && viaTable == viaRolling));
    }

    static long evaluatedCells;

    static long pathsMemoCounted(int[][] grid) {
        int rows = grid.length, cols = grid[0].length;
        return solveCounted(grid, rows - 1, cols - 1, new long[rows][cols], new boolean[rows][cols]);
    }

    static long solveCounted(int[][] grid, int r, int c, long[][] memo, boolean[][] computed) {
        if (r < 0 || c < 0 || grid[r][c] == 1) return 0;
        if (r == 0 && c == 0) return 1;
        if (computed[r][c]) return memo[r][c];
        evaluatedCells++;                                   // counts each cell whose value is actually calculated
        long result = solveCounted(grid, r - 1, c, memo, computed) + solveCounted(grid, r, c - 1, memo, computed);
        computed[r][c] = true;
        memo[r][c] = result;
        return result;
    }

    static long pathsRolling(int[][] grid) {
        int rows = grid.length, cols = grid[0].length;
        long[] row = new long[cols];
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (grid[r][c] == 1) row[c] = 0;
                else if (r == 0 && c == 0) row[c] = 1;
                else if (c > 0) row[c] += row[c - 1];
            }
        }
        return row[cols - 1];
    }

    static long best(long n, Map<Long, Long> memo) {
        if (n == 0) return 0;
        Long cached = memo.get(n);
        if (cached != null) return cached;
        long exchanged = best(n / 2, memo) + best(n / 3, memo) + best(n / 4, memo);
        long result = Math.max(n, exchanged);
        memo.put(n, result);
        return result;
    }

    static long waysMemo(int n, long[] memo) {
        if (n <= 1) return 1;
        if (memo[n] != 0) return memo[n];
        return memo[n] = waysMemo(n - 1, memo) + waysMemo(n - 2, memo);
    }

    static long waysTable(int n) {
        long prev = 1, current = 1;
        for (int i = 2; i <= n; i++) {
            long next = prev + current;
            prev = current;
            current = next;
        }
        return current;
    }

    static long pathsMemo(int[][] grid) {
        int rows = grid.length, cols = grid[0].length;
        long[][] memo = new long[rows][cols];
        boolean[][] computed = new boolean[rows][cols];
        return solve(grid, rows - 1, cols - 1, memo, computed);
    }

    static long solve(int[][] grid, int r, int c, long[][] memo, boolean[][] computed) {
        if (r < 0 || c < 0 || grid[r][c] == 1) return 0;
        if (r == 0 && c == 0) return 1;
        if (computed[r][c]) return memo[r][c];
        long result = solve(grid, r - 1, c, memo, computed) + solve(grid, r, c - 1, memo, computed);
        computed[r][c] = true;
        memo[r][c] = result;
        return result;
    }

    static long pathsTable(int[][] grid) {
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

    // exponential: literally walk every right/down route from (r, c) and count the ones that reach the bottom-right
    static long countPathsBruteForce(int[][] grid, int r, int c) {
        if (r >= grid.length || c >= grid[0].length || grid[r][c] == 1) return 0;
        if (r == grid.length - 1 && c == grid[0].length - 1) return 1;
        return countPathsBruteForce(grid, r + 1, c) + countPathsBruteForce(grid, r, c + 1);
    }
}
