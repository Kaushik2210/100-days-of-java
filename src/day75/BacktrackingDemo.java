import java.util.ArrayList;
import java.util.List;

public class BacktrackingDemo {

    public static void main(String[] args) {
        List<List<Integer>> results = new ArrayList<>();
        permute(new int[]{1, 2, 3}, new ArrayList<>(), new boolean[3], results);

        System.out.println("permutations of [1, 2, 3] (" + results.size() + " total):");
        for (List<Integer> permutation : results) {
            System.out.println("  " + permutation);
        }

        System.out.println();
        int[] knownSolutionCounts = {1, 0, 0, 2, 10, 4, 40, 92}; // published N-Queens solution counts for n = 1..8
        for (int n = 1; n <= 8; n++) {
            placeCalls = 0;
            int count = countNQueens(n);
            long bruteForceCandidates = (long) Math.pow(n, n); // one queen per row, any column: n^n complete placements
            System.out.println("N-Queens n=" + n + ": " + count + " solutions"
                + " (known: " + knownSolutionCounts[n - 1] + ", match: " + (count == knownSolutionCounts[n - 1]) + ")"
                + " | backtracking visited " + placeCalls + " nodes vs " + bruteForceCandidates + " brute-force candidates");
        }
    }

    static int placeCalls = 0;

    static int countNQueens(int n) {
        return place(0, n, new boolean[n], new boolean[2 * n], new boolean[2 * n]);
    }

    static int place(int row, int n, boolean[] columns, boolean[] diagonals, boolean[] antiDiagonals) {
        placeCalls++;
        if (row == n) return 1;

        int count = 0;
        for (int col = 0; col < n; col++) {
            int diagonal = row - col + n;
            int antiDiagonal = row + col;

            if (columns[col] || diagonals[diagonal] || antiDiagonals[antiDiagonal]) continue;

            columns[col] = diagonals[diagonal] = antiDiagonals[antiDiagonal] = true;
            count += place(row + 1, n, columns, diagonals, antiDiagonals);
            columns[col] = diagonals[diagonal] = antiDiagonals[antiDiagonal] = false;
        }
        return count;
    }

    static void permute(int[] nums, List<Integer> current, boolean[] used, List<List<Integer>> results) {
        if (current.size() == nums.length) {
            results.add(new ArrayList<>(current));
            return;
        }

        for (int i = 0; i < nums.length; i++) {
            if (used[i]) continue;

            used[i] = true;
            current.add(nums[i]);
            permute(nums, current, used, results);
            current.remove(current.size() - 1);
            used[i] = false;
        }
    }
}
