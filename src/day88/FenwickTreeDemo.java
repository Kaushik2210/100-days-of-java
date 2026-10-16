import java.util.Arrays;
import java.util.Random;

public class FenwickTreeDemo {

    public static void main(String[] args) {
        // the same array Day 87's segment tree used, so the answers can be compared directly
        int[] data = {2, 5, 1, 4, 9, 3, 7, 6};
        int n = data.length;
        FenwickTree fenwick = new FenwickTree(n);
        for (int i = 0; i < n; i++) fenwick.add(i + 1, data[i]); // positions are 1-based inside the tree
        System.out.println("array = " + Arrays.toString(data));

        System.out.println("which elements each cell covers (n = 8):");
        for (int i = 1; i <= n; i++) {
            int low = i & -i;
            System.out.println("  tree[" + i + "] covers positions " + (i - low + 1) + ".." + i + " (length " + low + ")");
        }

        System.out.println();
        System.out.println("prefixSum(7) = " + fenwick.prefixSum(7) + " (expected 2+5+1+4+9+3+7 = 31)");
        System.out.println("sum of positions 1..8 = " + fenwick.prefixSum(8) + " (expected 37)");
        System.out.println("range sum of positions 3..6 = " + fenwick.rangeSum(3, 6) + " (expected 1+4+9+3 = 17)");

        fenwick.add(4, 6); // position 4 goes from 4 to 10, matching Day 87's update(3, 10) on a 0-based array
        System.out.println("after adding 6 at position 4: range sum 3..6 = " + fenwick.rangeSum(3, 6) + " (expected 23)");
        System.out.println("                              range sum 1..8 = " + fenwick.rangeSum(1, 8) + " (expected 43)");

        System.out.println();
        int stepMismatches = 0;
        int updateTooLong = 0;
        int size = 1000;
        int maxUpdateSteps = 0;
        for (int i = 1; i <= size; i++) {
            if (queryStepCount(i) != Integer.bitCount(i)) stepMismatches++;
            int steps = updateStepCount(i, size);
            maxUpdateSteps = Math.max(maxUpdateSteps, steps);
            if (steps > 32 - Integer.numberOfLeadingZeros(size)) updateTooLong++;
        }
        System.out.println("complexity check for n = " + size + ":");
        System.out.println("  query steps for index i equal bitCount(i) in all cases: " + (stepMismatches == 0));
        System.out.println("  longest update walk = " + maxUpdateSteps + " steps (log2(1000) is about 10); over the "
            + (32 - Integer.numberOfLeadingZeros(size)) + "-step bound: " + updateTooLong + " times");
        System.out.println("  memory: Fenwick uses " + (100000 + 1) + " cells for n = 100000, Day 87's segment tree used " + (4 * 100000));

        System.out.println();
        Random random = new Random(88); // fixed seed so the run is repeatable
        int pointMismatches = 0;
        int pointQueries = 0;
        for (int t = 0; t < 400; t++) {
            int len = 1 + random.nextInt(30);
            long[] naive = new long[len + 1]; // 1-based
            FenwickTree tree = new FenwickTree(len);
            for (int op = 0; op < 50; op++) {
                if (random.nextBoolean()) {
                    int idx = 1 + random.nextInt(len);
                    int newValue = random.nextInt(41) - 20;
                    tree.add(idx, newValue - naive[idx]); // "set" is an add of the difference
                    naive[idx] = newValue;
                } else {
                    int l = 1 + random.nextInt(len);
                    int r = l + random.nextInt(len - l + 1);
                    long expected = 0;
                    for (int i = l; i <= r; i++) expected += naive[i];
                    pointQueries++;
                    if (tree.rangeSum(l, r) != expected) pointMismatches++;
                }
            }
        }
        System.out.println("Fenwick vs naive over 400 random arrays (" + pointQueries
            + " range queries mixed with set-style updates): " + pointMismatches + " mismatches");

        int buildMismatches = 0;
        for (int t = 0; t < 300; t++) {
            int len = 1 + random.nextInt(40);
            long[] oneBased = new long[len + 1];
            for (int i = 1; i <= len; i++) oneBased[i] = random.nextInt(101) - 50;

            FenwickTree byAdds = new FenwickTree(len);
            for (int i = 1; i <= len; i++) byAdds.add(i, oneBased[i]);
            FenwickTree linear = new FenwickTree(oneBased);
            if (!Arrays.equals(byAdds.cells(), linear.cells())) buildMismatches++;
        }
        System.out.println("linear O(n) build vs n separate add() calls, identical internal arrays in all but "
            + buildMismatches + " of 300 random cases");

        int rangeMismatches = 0;
        int rangeQueries = 0;
        for (int t = 0; t < 300; t++) {
            int len = 1 + random.nextInt(30);
            long[] naive = new long[len + 1];
            RangeFenwickTree rangeTree = new RangeFenwickTree(len);
            for (int op = 0; op < 60; op++) {
                int l = 1 + random.nextInt(len);
                int r = l + random.nextInt(len - l + 1);
                if (random.nextBoolean()) {
                    int x = random.nextInt(21) - 10;
                    for (int i = l; i <= r; i++) naive[i] += x;
                    rangeTree.rangeAdd(l, r, x);
                } else {
                    long expected = 0;
                    for (int i = l; i <= r; i++) expected += naive[i];
                    rangeQueries++;
                    if (rangeTree.rangeSum(l, r) != expected) rangeMismatches++;
                }
            }
        }
        System.out.println("two-tree range-add / range-sum vs naive over 300 random arrays (" + rangeQueries
            + " queries): " + rangeMismatches + " mismatches");

        System.out.println();
        int[] sample = {3, 1, 2, 5, 4};
        System.out.println("inversions in " + Arrays.toString(sample) + " = " + countInversions(sample, 5)
            + " (pairs: 3>1, 3>2, 5>4)");
        int inversionMismatches = 0;
        int bubbleMismatches = 0;
        int withDuplicates = 0;
        for (int t = 0; t < 300; t++) {
            int len = random.nextInt(16);
            int[] a = new int[len];
            for (int i = 0; i < len; i++) a[i] = 1 + random.nextInt(10);
            long fast = countInversions(a, 10);
            long brute = 0;
            for (int i = 0; i < len; i++) {
                for (int j = i + 1; j < len; j++) {
                    if (a[i] > a[j]) brute++;
                }
            }
            if (fast != brute) inversionMismatches++;
            if (bubbleSortSwaps(a.clone()) != fast) bubbleMismatches++;
            if (Arrays.stream(a).distinct().count() < len) withDuplicates++;
        }
        System.out.println("inversion count via Fenwick vs O(n^2) brute force over 300 random arrays ("
            + withDuplicates + " containing duplicates): " + inversionMismatches + " mismatches");
        System.out.println("number of swaps Day 54's bubble sort makes equals the inversion count: "
            + (bubbleMismatches == 0) + " (" + bubbleMismatches + " exceptions)");
    }

    static int queryStepCount(int i) {
        int steps = 0;
        while (i > 0) {
            steps++;
            i -= i & -i;
        }
        return steps;
    }

    static int updateStepCount(int i, int n) {
        int steps = 0;
        while (i <= n) {
            steps++;
            i += i & -i;
        }
        return steps;
    }

    static long countInversions(int[] a, int maxValue) {
        FenwickTree seen = new FenwickTree(maxValue);
        long inversions = 0;
        for (int i = 0; i < a.length; i++) {
            inversions += i - seen.prefixSum(a[i]);
            seen.add(a[i], 1);
        }
        return inversions;
    }

    static long bubbleSortSwaps(int[] arr) {
        long swaps = 0;
        for (int i = 0; i < arr.length - 1; i++) {
            for (int j = 0; j < arr.length - 1 - i; j++) {
                if (arr[j] > arr[j + 1]) {
                    int temp = arr[j];
                    arr[j] = arr[j + 1];
                    arr[j + 1] = temp;
                    swaps++;
                }
            }
        }
        return swaps;
    }
}

class FenwickTree {
    private final int n;
    private final long[] tree;

    FenwickTree(int n) {
        this.n = n;
        this.tree = new long[n + 1];
    }

    FenwickTree(long[] a) { // a is 1-based; a[0] is ignored
        this.n = a.length - 1;
        this.tree = new long[n + 1];
        for (int i = 1; i <= n; i++) {
            tree[i] += a[i];
            int parent = i + (i & -i);
            if (parent <= n) tree[parent] += tree[i];
        }
    }

    void add(int i, long delta) {
        while (i <= n) {
            tree[i] += delta;
            i += i & -i;
        }
    }

    long prefixSum(int i) {
        long sum = 0;
        while (i > 0) {
            sum += tree[i];
            i -= i & -i;
        }
        return sum;
    }

    long rangeSum(int l, int r) {
        return prefixSum(r) - prefixSum(l - 1);
    }

    long[] cells() {
        return tree.clone();
    }
}

class RangeFenwickTree {
    private final int n;
    private final long[] b1; // Fenwick tree over the differences d[j]
    private final long[] b2; // Fenwick tree over j * d[j]

    RangeFenwickTree(int n) {
        this.n = n;
        this.b1 = new long[n + 2];
        this.b2 = new long[n + 2];
    }

    private void add(long[] tree, int i, long delta) {
        while (i <= n) {
            tree[i] += delta;
            i += i & -i;
        }
    }

    private long sum(long[] tree, int i) {
        long total = 0;
        while (i > 0) {
            total += tree[i];
            i -= i & -i;
        }
        return total;
    }

    void rangeAdd(int l, int r, long x) {
        add(b1, l, x);
        add(b1, r + 1, -x);
        add(b2, l, x * l);
        add(b2, r + 1, -x * (r + 1));
    }

    private long prefix(int i) {
        return (long) (i + 1) * sum(b1, i) - sum(b2, i);
    }

    long rangeSum(int l, int r) {
        return prefix(r) - prefix(l - 1);
    }
}
