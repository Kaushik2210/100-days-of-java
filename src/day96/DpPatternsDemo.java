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
