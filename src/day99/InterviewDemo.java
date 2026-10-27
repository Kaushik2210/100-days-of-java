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

        problemTwoAndThree(random);
    }

    static void problemTwoAndThree(Random random) {
        System.out.println();
        System.out.println("== Problem 2: Top K Frequent Elements ==");
        int[] sample = {1, 1, 1, 2, 2, 3};
        System.out.println("[1, 1, 1, 2, 2, 3], k = 2: sort " + topKSort(sample, 2) + ", heap " + sortedCopy(topKHeap(sample, 2))
            + ", buckets " + topKBucket(sample, 2) + "   (expected 1 and 2)");
        int[] tied = {4, 4, 5, 5, 6, 6, 7};
        System.out.println("three-way tie at the boundary [4,4,5,5,6,6,7], k = 2: sort " + topKSort(tied, 2) + ", heap "
            + sortedCopy(topKHeap(tied, 2)) + ", buckets " + topKBucket(tied, 2)
            + "   (any two of 4, 5, 6 are valid, so tests must compare frequencies, not exact lists)");
        System.out.println("k larger than the number of distinct values: " + topKSort(new int[]{9, 9, 8}, 5) + " / "
            + sortedCopy(topKHeap(new int[]{9, 9, 8}, 5)) + " / " + topKBucket(new int[]{9, 9, 8}, 5) + "   (expected both values)");

        int trials = 5000;
        int invalid = 0;
        int tieCases = 0;
        for (int t = 0; t < trials; t++) {
            int[] nums = randomArray(random, 1 + random.nextInt(20), 0, 6);
            int k = 1 + random.nextInt(5);
            Map<Integer, Integer> counts = new HashMap<>();
            for (int x : nums) counts.merge(x, 1, Integer::sum);
            List<Integer> bestCounts = new ArrayList<>(counts.values());
            bestCounts.sort((a, b) -> b - a);
            List<Integer> expectedCounts = bestCounts.subList(0, Math.min(k, bestCounts.size()));
            if (k < bestCounts.size() && bestCounts.get(k - 1).equals(bestCounts.get(k))) tieCases++;

            if (!validTopK(counts, topKSort(nums, k), expectedCounts)
                || !validTopK(counts, topKHeap(nums, k), expectedCounts)
                || !validTopK(counts, topKBucket(nums, k), expectedCounts)) invalid++;
        }
        System.out.println("all three vs the sorted list of true frequencies over " + trials + " random cases (" + tieCases
            + " with a tie at the k-th place): " + invalid + " invalid answers");

        System.out.println("time on 2,000,000 elements, 50,000 distinct values, k = 10 (each includes the shared counting step):");
        int[] big = randomArray(random, 2_000_000, 0, 49_999);
        topKSort(big, 10); topKHeap(big, 10); topKBucket(big, 10);       // warm up
        long start = System.nanoTime();
        List<Integer> a = topKSort(big, 10);
        long sortMs = (System.nanoTime() - start) / 1_000_000;
        start = System.nanoTime();
        List<Integer> b = topKHeap(big, 10);
        long heapMs = (System.nanoTime() - start) / 1_000_000;
        start = System.nanoTime();
        List<Integer> c = topKBucket(big, 10);
        long bucketMs = (System.nanoTime() - start) / 1_000_000;
        Map<Integer, Integer> bigCounts = new HashMap<>();
        for (int x : big) bigCounts.merge(x, 1, Integer::sum);
        List<Integer> bigBest = new ArrayList<>(bigCounts.values());
        bigBest.sort((x, y) -> y - x);
        List<Integer> bigExpected = bigBest.subList(0, 10);
        System.out.println("  sort " + sortMs + " ms, heap " + heapMs + " ms, buckets " + bucketMs + " ms; all three valid: "
            + (validTopK(bigCounts, a, bigExpected) && validTopK(bigCounts, b, bigExpected) && validTopK(bigCounts, c, bigExpected)));
        System.out.println("  (the O(n) bucket version is the SLOWEST here: it allocates n + 1 = 2,000,001 lists to serve only 50,000 distinct values,");
        System.out.println("   and with k = 10 the heap's log k factor is tiny. Better big-O does not guarantee a faster run on a given input.)");

        System.out.println();
        System.out.println("== Problem 3: Merge Intervals ==");
        System.out.println("[[1,3],[2,6],[8,10],[15,18]] -> " + Arrays.deepToString(mergeIntervals(new int[][]{{1, 3}, {2, 6}, {8, 10}, {15, 18}}))
            + "   (expected [[1, 6], [8, 10], [15, 18]])");
        System.out.println("touching [[1,3],[3,5]] -> " + Arrays.deepToString(mergeIntervals(new int[][]{{1, 3}, {3, 5}}))
            + "   (expected one interval [[1, 5]])");
        System.out.println("contained [[1,10],[2,3]] -> " + Arrays.deepToString(mergeIntervals(new int[][]{{1, 10}, {2, 3}}))
            + "   (expected [[1, 10]]: this is the case a missing Math.max gets wrong)");
        System.out.println("unsorted input [[8,10],[1,3],[2,6]] -> " + Arrays.deepToString(mergeIntervals(new int[][]{{8, 10}, {1, 3}, {2, 6}}))
            + "   (expected [[1, 6], [8, 10]])");
        System.out.println("empty -> " + Arrays.deepToString(mergeIntervals(new int[][]{})) + ", single [[5,5]] -> "
            + Arrays.deepToString(mergeIntervals(new int[][]{{5, 5}})));

        int mismatches = 0;
        int mergedSomething = 0;
        int mutated = 0;
        for (int t = 0; t < trials; t++) {
            int n = random.nextInt(8);
            int[][] intervals = new int[n][];
            for (int i = 0; i < n; i++) {
                int s = random.nextInt(15);
                intervals[i] = new int[]{s, s + random.nextInt(5)};
            }
            int[][] copy = new int[n][];
            for (int i = 0; i < n; i++) copy[i] = intervals[i].clone();

            int[][] merged = mergeIntervals(intervals);
            if (!Arrays.deepEquals(merged, mergeByCoverage(intervals))) mismatches++;
            if (merged.length < n) mergedSomething++;
            if (!Arrays.deepEquals(intervals, copy)) mutated++;
        }
        System.out.println("sort-and-sweep vs an independent coverage-grid oracle (no sorting) over " + trials + " random lists ("
            + mergedSomething + " actually merged something): " + mismatches + " mismatches; caller's array modified in "
            + mutated + " cases");
    }

    static List<Integer> sortedCopy(List<Integer> list) {
        List<Integer> copy = new ArrayList<>(list);
        java.util.Collections.sort(copy);
        return copy;
    }

    // valid if the values are distinct, really present, and their true frequencies equal the best k frequencies
    static boolean validTopK(Map<Integer, Integer> counts, List<Integer> answer, List<Integer> expectedCounts) {
        if (answer.size() != expectedCounts.size() || new java.util.HashSet<>(answer).size() != answer.size()) return false;
        List<Integer> actual = new ArrayList<>();
        for (int value : answer) {
            Integer c = counts.get(value);
            if (c == null) return false;
            actual.add(c);
        }
        actual.sort((x, y) -> y - x);
        return actual.equals(expectedCounts);
    }

    static List<Integer> topKSort(int[] nums, int k) {
        Map<Integer, Integer> counts = new HashMap<>();
        for (int x : nums) counts.merge(x, 1, Integer::sum);
        List<Integer> values = new ArrayList<>(counts.keySet());
        values.sort((a, b) -> counts.get(b) - counts.get(a));
        return new ArrayList<>(values.subList(0, Math.min(k, values.size())));
    }

    static List<Integer> topKHeap(int[] nums, int k) {
        Map<Integer, Integer> counts = new HashMap<>();
        for (int x : nums) counts.merge(x, 1, Integer::sum);
        java.util.PriorityQueue<Integer> heap = new java.util.PriorityQueue<>((a, b) -> counts.get(a) - counts.get(b));
        for (int value : counts.keySet()) {
            heap.add(value);
            if (heap.size() > k) heap.poll();
        }
        return new ArrayList<>(heap);
    }

    static List<Integer> topKBucket(int[] nums, int k) {
        Map<Integer, Integer> counts = new HashMap<>();
        for (int x : nums) counts.merge(x, 1, Integer::sum);
        List<List<Integer>> buckets = new ArrayList<>();
        for (int i = 0; i <= nums.length; i++) buckets.add(new ArrayList<>());
        for (Map.Entry<Integer, Integer> e : counts.entrySet()) buckets.get(e.getValue()).add(e.getKey());

        List<Integer> result = new ArrayList<>();
        for (int f = nums.length; f >= 1 && result.size() < k; f--) {
            for (int value : buckets.get(f)) {
                if (result.size() == k) break;
                result.add(value);
            }
        }
        return result;
    }

    static int[][] mergeIntervals(int[][] intervals) {
        int[][] sorted = intervals.clone();
        Arrays.sort(sorted, (a, b) -> Integer.compare(a[0], b[0]));
        List<int[]> merged = new ArrayList<>();
        for (int[] current : sorted) {
            if (merged.isEmpty() || merged.get(merged.size() - 1)[1] < current[0]) {
                merged.add(new int[]{current[0], current[1]});
            } else {
                int[] last = merged.get(merged.size() - 1);
                last[1] = Math.max(last[1], current[1]);
            }
        }
        return merged.toArray(new int[0][]);
    }

    // independent oracle: on a grid of half-integers, mark every point a closed interval covers, then read off the runs.
    // Two intervals that touch at a shared endpoint leave no gap, while [1,2] and [3,4] leave an uncovered point between them.
    static int[][] mergeByCoverage(int[][] intervals) {
        if (intervals.length == 0) return new int[0][];
        int max = 0;
        for (int[] iv : intervals) max = Math.max(max, iv[1]);
        boolean[] covered = new boolean[2 * max + 3];
        for (int[] iv : intervals) {
            for (int p = 2 * iv[0]; p <= 2 * iv[1]; p++) covered[p] = true;
        }
        List<int[]> runs = new ArrayList<>();
        int p = 0;
        while (p < covered.length) {
            if (!covered[p]) { p++; continue; }
            int startPoint = p;
            while (p < covered.length && covered[p]) p++;
            runs.add(new int[]{startPoint / 2, (p - 1) / 2});
        }
        return runs.toArray(new int[0][]);
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
