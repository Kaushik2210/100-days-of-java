import java.util.Arrays;
import java.util.Random;

public class KnapsackDemo {

    public static void main(String[] args) {
        int[] weights = {1, 3, 4, 5};
        int[] values = {1, 4, 5, 7};
        int capacity = 7;

        System.out.println("greedy by value/weight ratio = " + greedyByRatio(weights, values, capacity) + " (Day 74's trap)");
        System.out.println("0/1 knapsack DP              = " + knapsack(weights, values, capacity));
        System.out.println("exhaustive search over all subsets = " + bruteForce(weights, values, capacity));

        System.out.println();
        Random random = new Random(11); // fixed seed so the run is repeatable
        int trials = 500;
        int mismatches = 0;
        int greedyWrong = 0;
        for (int t = 0; t < trials; t++) {
            int n = 1 + random.nextInt(10);
            int[] w = new int[n];
            int[] v = new int[n];
            for (int i = 0; i < n; i++) {
                w[i] = 1 + random.nextInt(10);
                v[i] = 1 + random.nextInt(20);
            }
            int cap = random.nextInt(25);

            int dp = knapsack(w, v, cap);
            if (dp != bruteForce(w, v, cap)) mismatches++;
            if (greedyByRatio(w, v, cap) != dp) greedyWrong++;
        }
        System.out.println("DP vs exhaustive search over " + trials + " random instances: " + mismatches + " mismatches");
        System.out.println("greedy-by-ratio was suboptimal in " + greedyWrong + " of those " + trials + " instances");

        System.out.println();
        System.out.println("1D space-optimized version on the example = " + knapsack1D(weights, values, capacity));

        int oneDimMismatches = 0;
        int unboundedMismatches = 0;
        for (int t = 0; t < trials; t++) {
            int n = 1 + random.nextInt(6);
            int[] w = new int[n];
            int[] v = new int[n];
            for (int i = 0; i < n; i++) {
                w[i] = 1 + random.nextInt(8);
                v[i] = 1 + random.nextInt(15);
            }
            int cap = random.nextInt(20);

            if (knapsack1D(w, v, cap) != knapsack(w, v, cap)) oneDimMismatches++;
            if (unboundedKnapsack(w, v, cap) != bruteForceUnbounded(w, v, cap)) unboundedMismatches++;
        }
        System.out.println("1D vs 2D over " + trials + " random instances: " + oneDimMismatches + " mismatches");
        System.out.println("unbounded DP vs exhaustive recursion over " + trials + " random instances: "
            + unboundedMismatches + " mismatches");

        System.out.println();
        int[] oneItemWeight = {3};
        int[] oneItemValue = {5};
        System.out.println("one item (weight 3, value 5), capacity 9:");
        System.out.println("  0/1 knapsack (take it once)        = " + knapsack1D(oneItemWeight, oneItemValue, 9));
        System.out.println("  unbounded (take it up to 3 times)  = " + unboundedKnapsack(oneItemWeight, oneItemValue, 9));
    }

    static int knapsack1D(int[] weights, int[] values, int capacity) {
        int[] dp = new int[capacity + 1];
        for (int i = 0; i < weights.length; i++) {
            for (int w = capacity; w >= weights[i]; w--) { // downward: each item is used at most once
                dp[w] = Math.max(dp[w], values[i] + dp[w - weights[i]]);
            }
        }
        return dp[capacity];
    }

    static int unboundedKnapsack(int[] weights, int[] values, int capacity) {
        int[] dp = new int[capacity + 1];
        for (int i = 0; i < weights.length; i++) {
            for (int w = weights[i]; w <= capacity; w++) { // upward: dp[w - weight] may already include this item
                dp[w] = Math.max(dp[w], values[i] + dp[w - weights[i]]);
            }
        }
        return dp[capacity];
    }

    // exhaustive recursion with unlimited copies, used only to cross-check the unbounded DP
    static int bruteForceUnbounded(int[] weights, int[] values, int capacity) {
        int best = 0;
        for (int i = 0; i < weights.length; i++) {
            if (weights[i] <= capacity) {
                best = Math.max(best, values[i] + bruteForceUnbounded(weights, values, capacity - weights[i]));
            }
        }
        return best;
    }

    static int knapsack(int[] weights, int[] values, int capacity) {
        int n = weights.length;
        int[][] dp = new int[n + 1][capacity + 1];

        for (int i = 1; i <= n; i++) {
            int weight = weights[i - 1];
            int value = values[i - 1];
            for (int w = 0; w <= capacity; w++) {
                dp[i][w] = dp[i - 1][w];
                if (weight <= w) {
                    dp[i][w] = Math.max(dp[i][w], value + dp[i - 1][w - weight]);
                }
            }
        }
        return dp[n][capacity];
    }

    static int greedyByRatio(int[] weights, int[] values, int capacity) {
        Integer[] order = new Integer[weights.length];
        for (int i = 0; i < order.length; i++) order[i] = i;
        Arrays.sort(order, (a, b) -> Double.compare((double) values[b] / weights[b], (double) values[a] / weights[a]));

        int total = 0;
        int remaining = capacity;
        for (int i : order) {
            if (weights[i] <= remaining) {
                remaining -= weights[i];
                total += values[i];
            }
        }
        return total;
    }

    // tries every one of the 2^n subsets -- only feasible for tiny n, used purely to cross-check the DP
    static int bruteForce(int[] weights, int[] values, int capacity) {
        int n = weights.length;
        int best = 0;
        for (int mask = 0; mask < (1 << n); mask++) {
            int weight = 0;
            int value = 0;
            for (int i = 0; i < n; i++) {
                if ((mask & (1 << i)) != 0) {
                    weight += weights[i];
                    value += values[i];
                }
            }
            if (weight <= capacity) best = Math.max(best, value);
        }
        return best;
    }
}
