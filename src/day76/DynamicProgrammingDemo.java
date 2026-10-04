import java.util.Arrays;

public class DynamicProgrammingDemo {

    static int naiveCalls = 0;
    static int memoCalls = 0;

    public static void main(String[] args) {
        naiveCalls = 0;
        long naiveResult = fibNaive(10);
        System.out.println("fibNaive(10)  = " + naiveResult + ", using " + naiveCalls + " calls (matches Day 52's 177)");

        memoCalls = 0;
        long memoResult = fibMemo(10, new long[11]);
        System.out.println("fibMemo(10)   = " + memoResult + ", using " + memoCalls + " calls");

        System.out.println("fibTable(10)  = " + fibTable(10));

        System.out.println();
        System.out.println("fibMemo(50)  = " + fibMemo(50, new long[51]) + " (known: 12586269025)");
        System.out.println("fibTable(50) = " + fibTable(50));
        System.out.println("fibTable(90) = " + fibTable(90) + " (known: 2880067194370816120)");

        System.out.println();
        int[] badCoins = {1, 3, 4};
        System.out.println("DP table for coins {1,3,4}, amount 0..6: " + Arrays.toString(coinTable(badCoins, 6)));
        System.out.println("minCoins({1,3,4}, 6) = " + minCoins(badCoins, 6)
            + " (Day 74's greedy got 3; true optimum is 2)");
        System.out.println("exhaustive check     = " + bruteForceMinCoins(badCoins, 6));

        int[] usCoins = {1, 5, 10, 25};
        System.out.println("minCoins({1,5,10,25}, 41) = " + minCoins(usCoins, 41) + " (greedy was also right here)");
        System.out.println("minCoins({4,6}, 7) = " + minCoins(new int[]{4, 6}, 7) + " (7 can't be made from 4s and 6s)");

        System.out.println();
        boolean allMatch = true;
        int[][] coinSets = {{1, 3, 4}, {1, 5, 10, 25}, {2, 5, 7}, {1, 6, 10}};
        for (int[] coins : coinSets) {
            for (int amount = 0; amount <= 20; amount++) {
                int expected = bruteForceMinCoins(coins, amount);
                int dp = minCoins(coins, amount);
                int normalized = expected == Integer.MAX_VALUE ? -1 : expected;
                if (dp != normalized) {
                    allMatch = false;
                    System.out.println("MISMATCH coins=" + Arrays.toString(coins) + " amount=" + amount);
                }
            }
        }
        System.out.println("DP agrees with exhaustive search on 4 coin sets x amounts 0..20: " + allMatch);
    }

    static int[] coinTable(int[] coins, int amount) {
        int[] table = new int[amount + 1];
        Arrays.fill(table, Integer.MAX_VALUE);
        table[0] = 0;
        for (int a = 1; a <= amount; a++) {
            for (int coin : coins) {
                if (coin <= a && table[a - coin] != Integer.MAX_VALUE) {
                    table[a] = Math.min(table[a], table[a - coin] + 1);
                }
            }
        }
        return table;
    }

    static int minCoins(int[] coins, int amount) {
        int result = coinTable(coins, amount)[amount];
        return result == Integer.MAX_VALUE ? -1 : result;
    }

    // exhaustive recursion, used only to cross-check the DP answers
    static int bruteForceMinCoins(int[] coins, int amount) {
        if (amount == 0) return 0;
        int best = Integer.MAX_VALUE;
        for (int coin : coins) {
            if (coin <= amount) {
                int sub = bruteForceMinCoins(coins, amount - coin);
                if (sub != Integer.MAX_VALUE) {
                    best = Math.min(best, sub + 1);
                }
            }
        }
        return best;
    }

    static long fibNaive(int n) {
        naiveCalls++;
        if (n <= 1) return n;
        return fibNaive(n - 1) + fibNaive(n - 2);
    }

    static long fibMemo(int n, long[] cache) {
        memoCalls++;
        if (n <= 1) return n;
        if (cache[n] != 0) return cache[n];

        cache[n] = fibMemo(n - 1, cache) + fibMemo(n - 2, cache);
        return cache[n];
    }

    static long fibTable(int n) {
        if (n <= 1) return n;
        long[] table = new long[n + 1];
        table[0] = 0;
        table[1] = 1;
        for (int i = 2; i <= n; i++) {
            table[i] = table[i - 1] + table[i - 2];
        }
        return table[n];
    }
}
