import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class BitManipulationDemo {

    public static void main(String[] args) {
        int n = 0b1010; // 10
        System.out.println("n = " + binary(n) + " (" + n + ")");
        System.out.println("isSet(n, 1) = " + isSet(n, 1) + ", isSet(n, 2) = " + isSet(n, 2));
        System.out.println("setBit(n, 0)    = " + binary(setBit(n, 0)));
        System.out.println("clearBit(n, 3)  = " + binary(clearBit(n, 3)));
        System.out.println("toggleBit(n, 2) = " + binary(toggleBit(n, 2)));

        System.out.println();
        int negative = -16;
        System.out.println("-16 >> 2  = " + (negative >> 2) + "  (signed: sign bit copied in, stays negative)");
        System.out.println("-16 >>> 28 = " + (negative >>> 28) + "  (unsigned: zeros shifted in)");

        System.out.println();
        Set<Integer> powersOfTwo = new HashSet<>();
        for (int k = 0; k <= 30; k++) powersOfTwo.add(1 << k);

        int powerMismatches = 0;
        for (int i = -100; i <= 5000; i++) {
            if (isPowerOfTwo(i) != powersOfTwo.contains(i)) powerMismatches++;
        }
        int[] edgeCases = {0, 1, 2, 3, 1 << 30, Integer.MAX_VALUE, Integer.MIN_VALUE};
        for (int edge : edgeCases) {
            if (isPowerOfTwo(edge) != powersOfTwo.contains(edge)) powerMismatches++;
        }
        System.out.println("isPowerOfTwo vs a set of all 31 positive powers, over -100..5000 plus edge cases: "
            + powerMismatches + " mismatches");
        System.out.println("isPowerOfTwo(Integer.MIN_VALUE) = " + isPowerOfTwo(Integer.MIN_VALUE)
            + " (only one bit set, but negative, so correctly rejected)");

        Random random = new Random(5); // fixed seed so the run is repeatable
        int bitCountMismatches = 0;
        for (int i = -1000; i <= 1000; i++) {
            if (countSetBits(i) != Integer.bitCount(i)) bitCountMismatches++;
        }
        for (int i = 0; i < 10_000; i++) {
            int value = random.nextInt(); // covers the whole int range, including negatives
            if (countSetBits(value) != Integer.bitCount(value)) bitCountMismatches++;
        }
        for (int edge : edgeCases) {
            if (countSetBits(edge) != Integer.bitCount(edge)) bitCountMismatches++;
        }
        System.out.println("countSetBits vs Integer.bitCount over ~11,000 values incl. negatives: "
            + bitCountMismatches + " mismatches");

        System.out.println();
        System.out.println("lowestSetBit(12) = " + lowestSetBit(12) + " (1100 -> 0100)");
        int lowestMismatches = 0;
        for (int i = -1000; i <= 1000; i++) {
            if (lowestSetBit(i) != Integer.lowestOneBit(i)) lowestMismatches++;
        }
        for (int edge : edgeCases) {
            if (lowestSetBit(edge) != Integer.lowestOneBit(edge)) lowestMismatches++;
        }
        System.out.println("lowestSetBit vs Integer.lowestOneBit over -1000..1000 plus edge cases: "
            + lowestMismatches + " mismatches");

        System.out.println();
        System.out.println("singleNumber([4, 1, 2, 1, 2]) = " + singleNumber(new int[]{4, 1, 2, 1, 2}) + " (expected 4)");
        int singleMismatches = 0;
        int singleTrials = 500;
        for (int t = 0; t < singleTrials; t++) {
            int pairs = random.nextInt(20);
            List<Integer> values = new ArrayList<>();
            int expectedSingle = random.nextInt(2001) - 1000; // can be negative or zero
            values.add(expectedSingle);
            Set<Integer> used = new HashSet<>();
            used.add(expectedSingle);
            while (used.size() < pairs + 1) {
                int candidate = random.nextInt(2001) - 1000;
                if (used.add(candidate)) {
                    values.add(candidate); // each paired value goes in twice
                    values.add(candidate);
                }
            }
            java.util.Collections.shuffle(values, random);
            int[] arr = values.stream().mapToInt(Integer::intValue).toArray();

            if (singleNumber(arr) != expectedSingle || singleNumberByCounting(arr) != expectedSingle) {
                singleMismatches++;
            }
        }
        System.out.println("singleNumber (XOR) vs HashMap counting vs known answer over " + singleTrials
            + " random shuffled arrays: " + singleMismatches + " mismatches");

        System.out.println();
        int[] items = {3, 5, 7, 9};
        System.out.println("all subsets of " + java.util.Arrays.toString(items) + " via bitmask:");
        List<List<Integer>> subsets = allSubsets(items);
        for (List<Integer> subset : subsets) {
            System.out.println("  " + subset);
        }
        System.out.println("count = " + subsets.size() + " (expected 2^4 = 16), all distinct: "
            + (new HashSet<>(subsets).size() == subsets.size()));

        int subsetMismatches = 0;
        for (int t = 0; t < 300; t++) {
            int size = random.nextInt(11);
            int[] nums = new int[size];
            for (int i = 0; i < size; i++) nums[i] = 1 + random.nextInt(9);
            int target = random.nextInt(30);
            if (countSubsetsWithSum(nums, target) != countSubsetsRecursive(nums, 0, target)) subsetMismatches++;
        }
        System.out.println("subsets-with-target-sum via bitmask vs plain recursion over 300 random cases: "
            + subsetMismatches + " mismatches");
    }

    static int lowestSetBit(int n) {
        return n & -n;
    }

    static int singleNumber(int[] nums) {
        int result = 0;
        for (int num : nums) {
            result ^= num;
        }
        return result;
    }

    // independent method: count occurrences in a map and return the value seen exactly once
    static int singleNumberByCounting(int[] nums) {
        Map<Integer, Integer> counts = new HashMap<>();
        for (int num : nums) counts.merge(num, 1, Integer::sum);
        for (Map.Entry<Integer, Integer> entry : counts.entrySet()) {
            if (entry.getValue() == 1) return entry.getKey();
        }
        throw new IllegalStateException("no single element");
    }

    static List<List<Integer>> allSubsets(int[] items) {
        int n = items.length;
        List<List<Integer>> result = new ArrayList<>();
        for (int mask = 0; mask < (1 << n); mask++) {
            List<Integer> subset = new ArrayList<>();
            for (int i = 0; i < n; i++) {
                if ((mask & (1 << i)) != 0) subset.add(items[i]);
            }
            result.add(subset);
        }
        return result;
    }

    static int countSubsetsWithSum(int[] nums, int target) {
        int count = 0;
        for (int mask = 0; mask < (1 << nums.length); mask++) {
            int sum = 0;
            for (int i = 0; i < nums.length; i++) {
                if ((mask & (1 << i)) != 0) sum += nums[i];
            }
            if (sum == target) count++;
        }
        return count;
    }

    // independent method: include-or-exclude recursion with no bit tricks
    static int countSubsetsRecursive(int[] nums, int index, int remaining) {
        if (index == nums.length) return remaining == 0 ? 1 : 0;
        return countSubsetsRecursive(nums, index + 1, remaining)                  // leave nums[index] out
            + countSubsetsRecursive(nums, index + 1, remaining - nums[index]);     // take nums[index]
    }

    static boolean isSet(int n, int i) {
        return (n & (1 << i)) != 0;
    }

    static int setBit(int n, int i) {
        return n | (1 << i);
    }

    static int clearBit(int n, int i) {
        return n & ~(1 << i);
    }

    static int toggleBit(int n, int i) {
        return n ^ (1 << i);
    }

    static boolean isPowerOfTwo(int n) {
        return n > 0 && (n & (n - 1)) == 0;
    }

    static int countSetBits(int n) {
        int count = 0;
        while (n != 0) {
            n &= n - 1;
            count++;
        }
        return count;
    }

    static String binary(int n) {
        String bits = Integer.toBinaryString(n);
        return "0b" + "0".repeat(Math.max(0, 4 - bits.length())) + bits;
    }
}
