import java.util.Arrays;
import java.util.Random;

public class DpPatternsDemo {

    public static void main(String[] args) {
        System.out.println("rob([1, 2, 3, 1]) = " + rob(new int[]{1, 2, 3, 1}) + " (expected 4: houses 1 and 3)");
        System.out.println("rob([2, 7, 9, 3, 1]) = " + rob(new int[]{2, 7, 9, 3, 1}) + " (expected 12: houses 1, 3, and 5)");
        System.out.println("rob([]) = " + rob(new int[]{}) + ", rob([5]) = " + rob(new int[]{5})
            + ", rob([5, 9]) = " + rob(new int[]{5, 9}) + " (expected 0, 5, 9)");

        Random random = new Random(96); // fixed seed so the run is repeatable
        int trials = 2000;
        int robMismatches = 0;
        for (int t = 0; t < trials; t++) {
            int[] money = randomArray(random, random.nextInt(15), 0, 20);
            if (rob(money) != robBruteForce(money)) robMismatches++;
        }
        System.out.println("house robber vs trying every set of non-adjacent houses over " + trials + " random rows (including empty): "
            + robMismatches + " mismatches");

        System.out.println();
        System.out.println("LIS of [10, 9, 2, 5, 3, 7, 101, 18] = " + lisQuadratic(new int[]{10, 9, 2, 5, 3, 7, 101, 18})
            + " / " + lisFast(new int[]{10, 9, 2, 5, 3, 7, 101, 18}) + " (expected 4: for example 2, 3, 7, 18)");
        System.out.println("LIS of [7, 7, 7, 7] = " + lisQuadratic(new int[]{7, 7, 7, 7}) + " / " + lisFast(new int[]{7, 7, 7, 7})
            + " (expected 1: equal values do not count as increasing)");
        System.out.println("LIS of [] = " + lisQuadratic(new int[]{}) + " / " + lisFast(new int[]{}) + " (expected 0)");

        int lisSlowMismatches = 0;
        int lisFastMismatches = 0;
        int duplicateArrays = 0;
        for (int t = 0; t < trials; t++) {
            int[] a = randomArray(random, random.nextInt(14), 0, 8);   // small value range, so duplicates are common
            int expected = lisBruteForce(a);
            if (lisQuadratic(a) != expected) lisSlowMismatches++;
            if (lisFast(a) != expected) lisFastMismatches++;
            if (Arrays.stream(a).distinct().count() < a.length) duplicateArrays++;
        }
        System.out.println("LIS vs trying every subsequence over " + trials + " random arrays (" + duplicateArrays
            + " containing duplicates): O(n^2) version " + lisSlowMismatches + " mismatches, O(n log n) version "
            + lisFastMismatches + " mismatches");

        int agreeMismatches = 0;
        for (int t = 0; t < 200; t++) {
            int[] a = randomArray(random, 1 + random.nextInt(2000), 0, 500);
            if (lisQuadratic(a) != lisFast(a)) agreeMismatches++;
        }
        int[] large = randomArray(random, 40000, 0, 1_000_000);
        long start = System.nanoTime();
        int slowAnswer = lisQuadratic(large);
        long slowMillis = (System.nanoTime() - start) / 1_000_000;
        start = System.nanoTime();
        int fastAnswer = lisFast(large);
        long fastMillis = (System.nanoTime() - start) / 1_000_000;
        System.out.println("the two versions disagree on " + agreeMismatches + " of 200 arrays up to length 2000");
        System.out.println("n = 40000: O(n^2) took " + slowMillis + " ms, O(n log n) took " + fastMillis + " ms, same answer: "
            + (slowAnswer == fastAnswer) + " (LIS length " + fastAnswer + ")");

        System.out.println();
        System.out.println("canPartition([1, 5, 11, 5]) = " + canPartition(new int[]{1, 5, 11, 5}) + " (expected true: 11 and 1+5+5)");
        System.out.println("canPartition([1, 2, 3, 5]) = " + canPartition(new int[]{1, 2, 3, 5}) + " (expected false: total 11 is odd)");
        System.out.println("canPartition([2, 2, 3, 5]) = " + canPartition(new int[]{2, 2, 3, 5}) + " (expected false: total 12 is even, but no subset makes 6)");

        int partitionMismatches = 0;
        int trueCases = 0;
        int evenTotalFalseCases = 0;
        for (int t = 0; t < trials; t++) {
            int[] nums = randomArray(random, random.nextInt(15), 1, 12);
            boolean expected = canPartitionBruteForce(nums);
            if (canPartition(nums) != expected) partitionMismatches++;
            if (expected) trueCases++;
            else if (Arrays.stream(nums).sum() % 2 == 0 && nums.length > 0) evenTotalFalseCases++;
        }
        System.out.println("partition vs trying every subset over " + trials + " random arrays (" + trueCases + " splittable, "
            + evenTotalFalseCases + " even-total but impossible): " + partitionMismatches + " mismatches");

        System.out.println();
        System.out.println("stock with cooldown [1, 2, 3, 0, 2] = " + maxProfitWithCooldown(new int[]{1, 2, 3, 0, 2})
            + " (expected 3: buy 1, sell 2, cool down, buy 0, sell 2)");
        System.out.println("stock with cooldown [1] = " + maxProfitWithCooldown(new int[]{1}) + ", [] = "
            + maxProfitWithCooldown(new int[]{}) + ", falling prices [5, 4, 3, 2] = "
            + maxProfitWithCooldown(new int[]{5, 4, 3, 2}) + " (expected 0, 0, 0)");
        System.out.println("rising prices [1, 2, 3, 4, 5] = " + maxProfitWithCooldown(new int[]{1, 2, 3, 4, 5})
            + " (expected 4: just buy on day 1 and sell on day 5)");

        int stockMismatches = 0;
        int profitableCases = 0;
        for (int t = 0; t < trials; t++) {
            int[] prices = randomArray(random, random.nextInt(13), 0, 10);
            int expected = stockBruteForce(prices, 0, false);
            if (maxProfitWithCooldown(prices) != expected) stockMismatches++;
            if (expected > 0) profitableCases++;
        }
        System.out.println("state-machine DP vs trying every buy/sell/skip sequence over " + trials + " random price lists ("
            + profitableCases + " profitable): " + stockMismatches + " mismatches");

        System.out.println();
        for (String s : new String[]{"bbbab", "cbbd", "a", "", "abcde"}) {
            System.out.println("longest palindromic subsequence of \"" + s + "\" = " + longestPalindromicSubsequence(s));
        }
        System.out.println("(expected 4, 2, 1, 0, 1)");

        int palindromeMismatches = 0;
        int lcsRelationMismatches = 0;
        for (int t = 0; t < trials; t++) {
            String s = randomString(random, random.nextInt(13), 3);
            int dp = longestPalindromicSubsequence(s);
            if (dp != lpsBruteForce(s)) palindromeMismatches++;
            if (dp != lcsLength(s, new StringBuilder(s).reverse().toString())) lcsRelationMismatches++;
        }
        int longerRelationMismatches = 0;
        for (int t = 0; t < 100; t++) {
            String s = randomString(random, 100 + random.nextInt(200), 4);
            if (longestPalindromicSubsequence(s) != lcsLength(s, new StringBuilder(s).reverse().toString())) longerRelationMismatches++;
        }
        System.out.println("interval DP vs trying every subsequence over " + trials + " random strings: "
            + palindromeMismatches + " mismatches");
        System.out.println("LPS(s) == LCS(s, reverse(s)) (Day 78's table): " + lcsRelationMismatches + " exceptions on short strings, "
            + longerRelationMismatches + " on 100 strings of length 100 to 300");
    }

    static String randomString(Random random, int length, int alphabet) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) sb.append((char) ('a' + random.nextInt(alphabet)));
        return sb.toString();
    }

    static int maxProfitWithCooldown(int[] prices) {
        if (prices.length == 0) return 0;
        final int NEVER = Integer.MIN_VALUE / 2;
        int hold = -prices[0];
        int sold = NEVER;
        int rest = 0;
        for (int i = 1; i < prices.length; i++) {
            int newHold = Math.max(hold, rest - prices[i]);
            int newSold = hold + prices[i];
            int newRest = Math.max(rest, sold);
            hold = newHold;
            sold = newSold;
            rest = newRest;
        }
        return Math.max(sold, rest);
    }

    // the rules applied literally, with no caching: skip the day, buy (if free), or sell (which forces a skipped day after)
    static int stockBruteForce(int[] prices, int day, boolean holding) {
        if (day >= prices.length) return 0;
        int best = stockBruteForce(prices, day + 1, holding);
        if (holding) {
            best = Math.max(best, prices[day] + stockBruteForce(prices, day + 2, false));
        } else {
            best = Math.max(best, -prices[day] + stockBruteForce(prices, day + 1, true));
        }
        return best;
    }

    static int longestPalindromicSubsequence(String s) {
        int n = s.length();
        if (n == 0) return 0;
        int[][] dp = new int[n][n];
        for (int i = n - 1; i >= 0; i--) {
            dp[i][i] = 1;
            for (int j = i + 1; j < n; j++) {
                if (s.charAt(i) == s.charAt(j)) {
                    dp[i][j] = 2 + (i + 1 <= j - 1 ? dp[i + 1][j - 1] : 0);
                } else {
                    dp[i][j] = Math.max(dp[i + 1][j], dp[i][j - 1]);
                }
            }
        }
        return dp[0][n - 1];
    }

    // every subsequence as a bitmask, keeping the longest one that reads the same in both directions
    static int lpsBruteForce(String s) {
        int n = s.length();
        int best = 0;
        for (int mask = 0; mask < (1 << n); mask++) {
            StringBuilder picked = new StringBuilder();
            for (int i = 0; i < n; i++) {
                if ((mask & (1 << i)) != 0) picked.append(s.charAt(i));
            }
            if (picked.length() > best && picked.toString().contentEquals(new StringBuilder(picked).reverse())) {
                best = picked.length();
            }
        }
        return best;
    }

    // Day 78's longest-common-subsequence table
    static int lcsLength(String a, String b) {
        int[][] dp = new int[a.length() + 1][b.length() + 1];
        for (int i = 1; i <= a.length(); i++) {
            for (int j = 1; j <= b.length(); j++) {
                if (a.charAt(i - 1) == b.charAt(j - 1)) dp[i][j] = dp[i - 1][j - 1] + 1;
                else dp[i][j] = Math.max(dp[i - 1][j], dp[i][j - 1]);
            }
        }
        return dp[a.length()][b.length()];
    }

    static int lisQuadratic(int[] a) {
        int n = a.length;
        int[] len = new int[n];
        int best = 0;
        for (int i = 0; i < n; i++) {
            len[i] = 1;
            for (int j = 0; j < i; j++) {
                if (a[j] < a[i]) len[i] = Math.max(len[i], len[j] + 1);
            }
            best = Math.max(best, len[i]);
        }
        return best;
    }

    static int lisFast(int[] a) {
        int[] tails = new int[a.length];
        int size = 0;
        for (int x : a) {
            int lo = 0, hi = size;
            while (lo < hi) {
                int mid = (lo + hi) / 2;
                if (tails[mid] < x) lo = mid + 1;
                else hi = mid;
            }
            tails[lo] = x;
            if (lo == size) size++;
        }
        return size;
    }

    // every subsequence as a bitmask, keeping the longest one that is strictly increasing
    static int lisBruteForce(int[] a) {
        int n = a.length;
        int best = 0;
        for (int mask = 0; mask < (1 << n); mask++) {
            int previous = Integer.MIN_VALUE;
            boolean increasing = true;
            int count = 0;
            for (int i = 0; i < n && increasing; i++) {
                if ((mask & (1 << i)) == 0) continue;
                if (a[i] <= previous) increasing = false;
                previous = a[i];
                count++;
            }
            if (increasing) best = Math.max(best, count);
        }
        return best;
    }

    static boolean canPartition(int[] nums) {
        int total = 0;
        for (int v : nums) total += v;
        if (total % 2 != 0) return false;
        int target = total / 2;

        boolean[] reachable = new boolean[target + 1];
        reachable[0] = true;
        for (int v : nums) {
            for (int s = target; s >= v; s--) {
                if (reachable[s - v]) reachable[s] = true;
            }
        }
        return reachable[target];
    }

    static boolean canPartitionBruteForce(int[] nums) {
        int total = Arrays.stream(nums).sum();
        if (total % 2 != 0) return false;
        for (int mask = 0; mask < (1 << nums.length); mask++) {
            int sum = 0;
            for (int i = 0; i < nums.length; i++) {
                if ((mask & (1 << i)) != 0) sum += nums[i];
            }
            if (sum * 2 == total) return true;
        }
        return false;
    }

    static int[] randomArray(Random random, int length, int low, int high) {
        int[] a = new int[length];
        for (int i = 0; i < length; i++) a[i] = low + random.nextInt(high - low + 1);
        return a;
    }

    static int rob(int[] money) {
        int twoBack = 0;
        int oneBack = 0;
        for (int amount : money) {
            int current = Math.max(oneBack, twoBack + amount);
            twoBack = oneBack;
            oneBack = current;
        }
        return oneBack;
    }

    // every subset of houses as a bitmask; a subset is allowed only if it contains no two neighbors
    static int robBruteForce(int[] money) {
        int n = money.length;
        int best = 0;
        for (int mask = 0; mask < (1 << n); mask++) {
            if ((mask & (mask >> 1)) != 0) continue;
            int total = 0;
            for (int i = 0; i < n; i++) {
                if ((mask & (1 << i)) != 0) total += money[i];
            }
            best = Math.max(best, total);
        }
        return best;
    }
}
