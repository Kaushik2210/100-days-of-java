import java.util.Arrays;
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
