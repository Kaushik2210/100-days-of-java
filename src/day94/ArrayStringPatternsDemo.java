import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class ArrayStringPatternsDemo {

    // Pattern 1: prefix sums + hash map. Count subarrays whose sum is exactly k (negatives allowed, so no window).
    static int subarraysWithSum(int[] a, int k) {
        Map<Integer, Integer> seen = new HashMap<>();
        seen.put(0, 1); // the empty prefix
        int prefix = 0, count = 0;
        for (int x : a) {
            prefix += x;
            count += seen.getOrDefault(prefix - k, 0);
            seen.merge(prefix, 1, Integer::sum);
        }
        return count;
    }

    static int subarraysWithSumBrute(int[] a, int k) {
        int count = 0;
        for (int i = 0; i < a.length; i++) {
            int sum = 0;
            for (int j = i; j < a.length; j++) {
                sum += a[j];
                if (sum == k) count++;
            }
        }
        return count;
    }

    // Pattern 2: Kadane's scan. Best non-empty subarray sum: extend the running run or restart at x.
    static int maxSubarray(int[] a) {
        int best = a[0], run = a[0];
        for (int i = 1; i < a.length; i++) {
            run = Math.max(a[i], run + a[i]);
            best = Math.max(best, run);
        }
        return best;
    }

    static int maxSubarrayBrute(int[] a) {
        int best = Integer.MIN_VALUE;
        for (int i = 0; i < a.length; i++) {
            int sum = 0;
            for (int j = i; j < a.length; j++) {
                sum += a[j];
                best = Math.max(best, sum);
            }
        }
        return best;
    }

    // Pattern 3: in-place partitioning (Dutch national flag). Sort 0s, 1s, 2s in one pass.
    static void sortColors(int[] a) {
        int low = 0, mid = 0, high = a.length - 1;
        while (mid <= high) {
            if (a[mid] == 0) swap(a, low++, mid++);
            else if (a[mid] == 2) swap(a, mid, high--); // do not advance mid: the swapped-in value is unexamined
            else mid++;
        }
    }

    static void swap(int[] a, int i, int j) {
        int t = a[i];
        a[i] = a[j];
        a[j] = t;
    }

    // Pattern 4: monotonic stack. For each element, the distance to the next strictly greater element (0 if none).
    static int[] daysUntilWarmer(int[] t) {
        int[] answer = new int[t.length];
        Deque<Integer> stack = new ArrayDeque<>(); // indexes whose values are non-increasing
        for (int i = 0; i < t.length; i++) {
            while (!stack.isEmpty() && t[stack.peek()] < t[i]) {
                int j = stack.pop();
                answer[j] = i - j;
            }
            stack.push(i);
        }
        return answer;
    }

    static int[] daysUntilWarmerBrute(int[] t) {
        int[] answer = new int[t.length];
        for (int i = 0; i < t.length; i++)
            for (int j = i + 1; j < t.length; j++)
                if (t[j] > t[i]) {
                    answer[i] = j - i;
                    break;
                }
        return answer;
    }

    // Pattern 5: frequency counting. Two strings are anagrams if their letter counts cancel to zero.
    static boolean isAnagram(String s, String t) {
        if (s.length() != t.length()) return false;
        int[] counts = new int[26];
        for (int i = 0; i < s.length(); i++) {
            counts[s.charAt(i) - 'a']++;
            counts[t.charAt(i) - 'a']--;
        }
        for (int c : counts) if (c != 0) return false;
        return true;
    }

    static boolean isAnagramBrute(String s, String t) {
        char[] a = s.toCharArray(), b = t.toCharArray();
        Arrays.sort(a);
        Arrays.sort(b);
        return Arrays.equals(a, b);
    }

    // Pattern 6: difference array. Apply many range additions in O(1) each, then rebuild with one prefix sum.
    static int[] applyRangeAdds(int n, int[][] updates) {
        int[] diff = new int[n + 1];
        for (int[] u : updates) {
            diff[u[0]] += u[2];
            diff[u[1] + 1] -= u[2];
        }
        int[] result = new int[n];
        int run = 0;
        for (int i = 0; i < n; i++) {
            run += diff[i];
            result[i] = run;
        }
        return result;
    }

    static int[] applyRangeAddsBrute(int n, int[][] updates) {
        int[] result = new int[n];
        for (int[] u : updates)
            for (int i = u[0]; i <= u[1]; i++) result[i] += u[2];
        return result;
    }

    static int[] randomArray(Random random, int maxLen, int bound) {
        int[] a = new int[1 + random.nextInt(maxLen)];
        for (int i = 0; i < a.length; i++) a[i] = random.nextInt(2 * bound + 1) - bound;
        return a;
    }

    static String randomWord(Random random, int len) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < len; i++) sb.append((char) ('a' + random.nextInt(3)));
        return sb.toString();
    }

    public static void main(String[] args) {
        System.out.println("subarrays summing to 2 in [1, 1, 1] = " + subarraysWithSum(new int[] {1, 1, 1}, 2) + " (expected 2)");
        System.out.println("subarrays summing to 0 in [1, -1, 0] = " + subarraysWithSum(new int[] {1, -1, 0}, 0) + " (expected 3)");
        System.out.println("max subarray of [-2, 1, -3, 4, -1, 2, 1, -5, 4] = "
            + maxSubarray(new int[] {-2, 1, -3, 4, -1, 2, 1, -5, 4}) + " (expected 6)");
        System.out.println("max subarray of all negatives [-3, -1, -2] = " + maxSubarray(new int[] {-3, -1, -2}) + " (expected -1)");
        int[] colors = {2, 0, 2, 1, 1, 0};
        sortColors(colors);
        System.out.println("sortColors [2, 0, 2, 1, 1, 0] -> " + Arrays.toString(colors));
        System.out.println("days until warmer for [73, 74, 75, 71, 69, 72, 76, 73] -> "
            + Arrays.toString(daysUntilWarmer(new int[] {73, 74, 75, 71, 69, 72, 76, 73})) + " (expected [1, 1, 4, 2, 1, 1, 0, 0])");
        System.out.println("anagram(listen, silent) = " + isAnagram("listen", "silent") + ", anagram(rat, car) = " + isAnagram("rat", "car"));
        System.out.println("range adds on 5 cells, +2 over [1,3] and +3 over [2,4] -> "
            + Arrays.toString(applyRangeAdds(5, new int[][] {{1, 3, 2}, {2, 4, 3}})) + " (expected [0, 2, 5, 5, 3])");

        Random random = new Random(94); // fixed seed so the run is repeatable
        int trials = 2000;
        int[] mismatches = new int[6];
        for (int t = 0; t < trials; t++) {
            int[] a = randomArray(random, 12, 5);
            int k = random.nextInt(11) - 5;
            if (subarraysWithSum(a, k) != subarraysWithSumBrute(a, k)) mismatches[0]++;
            if (maxSubarray(a) != maxSubarrayBrute(a)) mismatches[1]++;

            int[] flag = new int[1 + random.nextInt(12)];
            for (int i = 0; i < flag.length; i++) flag[i] = random.nextInt(3);
            int[] expected = flag.clone();
            Arrays.sort(expected);
            sortColors(flag);
            if (!Arrays.equals(flag, expected)) mismatches[2]++;

            int[] temps = randomArray(random, 12, 5);
            if (!Arrays.equals(daysUntilWarmer(temps), daysUntilWarmerBrute(temps))) mismatches[3]++;

            String s = randomWord(random, 1 + random.nextInt(6));
            String u = random.nextBoolean() ? randomWord(random, s.length()) : shuffle(random, s);
            if (isAnagram(s, u) != isAnagramBrute(s, u)) mismatches[4]++;

            int n = 1 + random.nextInt(10);
            int[][] updates = new int[random.nextInt(6)][];
            for (int i = 0; i < updates.length; i++) {
                int l = random.nextInt(n), r = l + random.nextInt(n - l);
                updates[i] = new int[] {l, r, random.nextInt(9) - 4};
            }
            if (!Arrays.equals(applyRangeAdds(n, updates), applyRangeAddsBrute(n, updates))) mismatches[5]++;
        }
        String[] names = {"prefix-sum count", "Kadane", "Dutch flag", "monotonic stack", "anagram counts", "difference array"};
        System.out.println("each pattern vs a brute-force version over " + trials + " random cases:");
        for (int i = 0; i < names.length; i++)
            System.out.println("  " + names[i] + ": " + (mismatches[i] == 0 ? "all match" : mismatches[i] + " MISMATCHES"));

        int n = 20_000;
        int[] big = new int[n];
        for (int i = 0; i < n; i++) big[i] = random.nextInt(21) - 10;
        long t0 = System.nanoTime();
        int fast = subarraysWithSum(big, 7);
        long t1 = System.nanoTime();
        int slow = subarraysWithSumBrute(big, 7);
        long t2 = System.nanoTime();
        System.out.printf("n=%,d count subarrays with sum 7: prefix-map %d in %.1f ms, brute force %d in %.1f ms%n",
            n, fast, (t1 - t0) / 1e6, slow, (t2 - t1) / 1e6);
    }

    static String shuffle(Random random, String s) {
        char[] c = s.toCharArray();
        for (int i = c.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            char t = c[i];
            c[i] = c[j];
            c[j] = t;
        }
        return new String(c);
    }
}
