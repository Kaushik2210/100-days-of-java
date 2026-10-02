import java.util.Arrays;

public class GreedyDemo {

    public static void main(String[] args) {
        // {start, end} pairs; the optimal non-overlapping set here is (1,2),(3,4),(5,7),(8,9) = 4 activities
        int[][] activities = {{1, 3}, {2, 4}, {3, 5}, {1, 2}, {5, 7}, {8, 9}, {5, 9}};
        System.out.println("maxNonOverlapping = " + maxNonOverlapping(activities));

        System.out.println();
        int[] usCoins = {1, 5, 10, 25};
        System.out.println("greedyCoinCount({1,5,10,25}, 41) = " + greedyCoinCount(usCoins, 41)
            + " (optimal: 25+10+5+1 = 4 coins)");

        int[] badCoins = {1, 3, 4};
        int greedyResult = greedyCoinCount(badCoins, 6);
        int optimalResult = bruteForceMinCoins(badCoins, 6);
        System.out.println("greedyCoinCount({1,3,4}, 6) = " + greedyResult + " (greedy picks 4+1+1)");
        System.out.println("bruteForceMinCoins({1,3,4}, 6) = " + optimalResult + " (true optimum is 3+3)");
        System.out.println("greedy is actually optimal here: " + (greedyResult == optimalResult));
    }

    static int greedyCoinCount(int[] denominations, int amount) {
        int count = 0;
        for (int i = denominations.length - 1; i >= 0; i--) {
            while (amount >= denominations[i]) {
                amount -= denominations[i];
                count++;
            }
        }
        return amount == 0 ? count : -1;
    }

    // exhaustive search over every combination -- used only to find the TRUE optimum for comparison
    static int bruteForceMinCoins(int[] denominations, int amount) {
        if (amount == 0) return 0;
        int best = Integer.MAX_VALUE;
        for (int coin : denominations) {
            if (coin <= amount) {
                int sub = bruteForceMinCoins(denominations, amount - coin);
                if (sub != Integer.MAX_VALUE) {
                    best = Math.min(best, sub + 1);
                }
            }
        }
        return best;
    }

    static int maxNonOverlapping(int[][] activities) {
        Arrays.sort(activities, (a, b) -> a[1] - b[1]);

        int count = 0;
        int lastEnd = Integer.MIN_VALUE;
        for (int[] activity : activities) {
            int start = activity[0];
            int end = activity[1];
            if (start >= lastEnd) {
                count++;
                lastEnd = end;
            }
        }
        return count;
    }
}
