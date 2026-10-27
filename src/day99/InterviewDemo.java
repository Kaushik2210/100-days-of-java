import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class InterviewDemo {

    public static void main(String[] args) {
        System.out.println("== Problem 1: Two Sum ==");
        show("[2, 7, 11, 15], target 9", twoSumHash(new int[]{2, 7, 11, 15}, 9), "indices 0 and 1");
        show("[3, 3], target 6 (two different elements)", twoSumHash(new int[]{3, 3}, 6), "indices 0 and 1");
        show("[3], target 6 (one element must not pair with itself)", twoSumHash(new int[]{3}, 6), "no answer");
        show("[-3, 4, 3, 90], target 0 (negatives)", twoSumHash(new int[]{-3, 4, 3, 90}, 0), "indices 0 and 2");
        show("[0, 4, 0], target 0 (zero target)", twoSumHash(new int[]{0, 4, 0}, 0), "indices 0 and 2");
        show("[1, 2, 3], target 100 (no pair)", twoSumHash(new int[]{1, 2, 3}, 100), "no answer");
        show("[], target 5 (empty)", twoSumHash(new int[]{}, 5), "no answer");

        Random random = new Random(99); // fixed seed so the run is repeatable
        int trials = 5000;
        int existenceMismatches = 0;
        int invalidAnswers = 0;
        int withAnswer = 0;
        for (int t = 0; t < trials; t++) {
            int[] nums = randomArray(random, random.nextInt(12), -6, 6);   // small range: duplicates and zero are common
            int target = random.nextInt(15) - 7;
            boolean exists = twoSumBrute(nums, target) != null;
            if (exists) withAnswer++;

            int[] viaHash = twoSumHash(nums, target);
            int[] viaSorted = twoSumSorted(nums, target);
            if ((viaHash != null) != exists || (viaSorted != null) != exists) existenceMismatches++;
            if (viaHash != null && !validPair(nums, target, viaHash)) invalidAnswers++;
            if (viaSorted != null && !validPair(nums, target, viaSorted)) invalidAnswers++;
        }
        System.out.println("hash map and sorted two-pointer vs brute force over " + trials + " random cases (" + withAnswer
            + " had an answer): " + existenceMismatches + " disagreements about whether a pair exists, "
            + invalidAnswers + " returned pairs that were not valid");

        System.out.println();
        System.out.println("time vs space as n grows (distinct even numbers in random order, with the matching pair placed last,");
        System.out.println("which is the worst case for a scan; the sorted version must really sort, since the input is not presorted):");
        for (int n : new int[]{2_000, 8_000, 32_000}) {
            int[] nums = new int[n];
            for (int i = 0; i < n; i++) nums[i] = 2 * i;
            int target = nums[n - 1] + nums[n - 2];
            for (int i = n - 3; i > 0; i--) {                           // shuffle the first n-2 elements only
                int j = random.nextInt(i + 1);
                int temp = nums[i]; nums[i] = nums[j]; nums[j] = temp;
            }

            twoSumHash(nums, target);                                  // warm up so the timings below are steady-state
            twoSumBrute(nums, target);
            long start = System.nanoTime();
            int[] a = twoSumBrute(nums, target);
            long bruteMicros = (System.nanoTime() - start) / 1_000;
            start = System.nanoTime();
            int[] b = twoSumHash(nums, target);
            long hashMicros = (System.nanoTime() - start) / 1_000;
            start = System.nanoTime();
            int[] c = twoSumSorted(nums, target);
            long sortedMicros = (System.nanoTime() - start) / 1_000;
            System.out.printf("  n = %,6d: brute O(n^2) %,8d us | hash O(n) time + O(n) space %,6d us | sort + two pointers %,6d us   all valid: %b%n",
                n, bruteMicros, hashMicros, sortedMicros, validPair(nums, target, a) && validPair(nums, target, b) && validPair(nums, target, c));
        }
    }

    static void show(String label, int[] result, String expected) {
        System.out.println("  " + label + " -> " + (result == null ? "no answer" : Arrays.toString(result)) + "   (expected " + expected + ")");
    }

    static int[] randomArray(Random random, int length, int low, int high) {
        int[] a = new int[length];
        for (int i = 0; i < length; i++) a[i] = low + random.nextInt(high - low + 1);
        return a;
    }

    static boolean validPair(int[] nums, int target, int[] pair) {
        return pair[0] != pair[1] && pair[0] >= 0 && pair[1] >= 0 && pair[0] < nums.length && pair[1] < nums.length
            && nums[pair[0]] + nums[pair[1]] == target;
    }

    static int[] twoSumBrute(int[] nums, int target) {
        for (int i = 0; i < nums.length; i++)
            for (int j = i + 1; j < nums.length; j++)
                if (nums[i] + nums[j] == target) return new int[]{i, j};
        return null;
    }

    static int[] twoSumHash(int[] nums, int target) {
        Map<Integer, Integer> seen = new HashMap<>();
        for (int i = 0; i < nums.length; i++) {
            Integer partner = seen.get(target - nums[i]);
            if (partner != null) return new int[]{partner, i};
            seen.put(nums[i], i);
        }
        return null;
    }

    // sort a list of indices by value, then run Day 71's opposite-ends pointers; the original indices are kept
    static int[] twoSumSorted(int[] nums, int target) {
        Integer[] order = new Integer[nums.length];
        for (int i = 0; i < order.length; i++) order[i] = i;
        Arrays.sort(order, (a, b) -> Integer.compare(nums[a], nums[b]));

        int left = 0, right = nums.length - 1;
        while (left < right) {
            int sum = nums[order[left]] + nums[order[right]];
            if (sum == target) return new int[]{order[left], order[right]};
            if (sum < target) left++;
            else right--;
        }
        return null;
    }
}
