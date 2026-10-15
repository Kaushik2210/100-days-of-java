import java.util.Arrays;
import java.util.Random;
import java.util.function.IntBinaryOperator;

public class SegmentTreeDemo {

    public static void main(String[] args) {
        int[] data = {2, 5, 1, 4, 9, 3, 7, 6};
        SegmentTree tree = new SegmentTree(data);
        System.out.println("array = " + Arrays.toString(data));

        System.out.println("sum of [0, 7] = " + tree.query(0, 7) + " (expected 37)");
        System.out.println("sum of [2, 5] = " + tree.query(2, 5) + " (expected 1 + 4 + 9 + 3 = 17)");
        System.out.println("sum of [4, 4] = " + tree.query(4, 4) + " (a single element)");

        tree.update(3, 10); // data[3]: 4 -> 10
        System.out.println("after update(3, 10): sum of [2, 5] = " + tree.query(2, 5) + " (expected 23)");
        System.out.println("                     sum of [0, 7] = " + tree.query(0, 7) + " (expected 43)");

        System.out.println();
        int[] values = {12, 18, 30, 9, 27, 6, 15, 24};
        GenericSegmentTree min = new GenericSegmentTree(values, Math::min, Integer.MAX_VALUE);
        GenericSegmentTree max = new GenericSegmentTree(values, Math::max, Integer.MIN_VALUE);
        GenericSegmentTree gcd = new GenericSegmentTree(values, SegmentTreeDemo::gcd, 0);
        System.out.println("values = " + Arrays.toString(values));
        System.out.println("min of [0, 3] = " + min.query(0, 3) + " (expected 9)");
        System.out.println("max of [2, 6] = " + max.query(2, 6) + " (expected 30)");
        System.out.println("gcd of [0, 2] = " + gcd.query(0, 2) + " (gcd(12, 18, 30) = 6)");
        System.out.println("gcd of [3, 4] = " + gcd.query(3, 4) + " (gcd(9, 27) = 9)");

        System.out.println();
        Random random = new Random(87); // fixed seed so the run is repeatable
        int trials = 300;
        int sumMismatch = 0;
        int minMismatch = 0;
        int maxMismatch = 0;
        int gcdMismatch = 0;
        int queriesChecked = 0;
        for (int t = 0; t < trials; t++) {
            int n = 1 + random.nextInt(20);
            int[] naive = new int[n];
            for (int i = 0; i < n; i++) naive[i] = 1 + random.nextInt(50);

            GenericSegmentTree sumTree = new GenericSegmentTree(naive, Integer::sum, 0);
            GenericSegmentTree minTree = new GenericSegmentTree(naive, Math::min, Integer.MAX_VALUE);
            GenericSegmentTree maxTree = new GenericSegmentTree(naive, Math::max, Integer.MIN_VALUE);
            GenericSegmentTree gcdTree = new GenericSegmentTree(naive, SegmentTreeDemo::gcd, 0);

            for (int op = 0; op < 40; op++) {
                if (random.nextBoolean()) {
                    int idx = random.nextInt(n);
                    int value = 1 + random.nextInt(50);
                    naive[idx] = value;
                    sumTree.update(idx, value);
                    minTree.update(idx, value);
                    maxTree.update(idx, value);
                    gcdTree.update(idx, value);
                } else {
                    int l = random.nextInt(n);
                    int r = l + random.nextInt(n - l);
                    queriesChecked++;
                    int expectedSum = 0;
                    int expectedMin = Integer.MAX_VALUE;
                    int expectedMax = Integer.MIN_VALUE;
                    int expectedGcd = 0;
                    for (int i = l; i <= r; i++) {
                        expectedSum += naive[i];
                        expectedMin = Math.min(expectedMin, naive[i]);
                        expectedMax = Math.max(expectedMax, naive[i]);
                        expectedGcd = gcd(expectedGcd, naive[i]);
                    }
                    if (sumTree.query(l, r) != expectedSum) sumMismatch++;
                    if (minTree.query(l, r) != expectedMin) minMismatch++;
                    if (maxTree.query(l, r) != expectedMax) maxMismatch++;
                    if (gcdTree.query(l, r) != expectedGcd) gcdMismatch++;
                }
            }
        }
        System.out.println("segment trees vs naive recomputation over " + trials + " random arrays ("
            + queriesChecked + " range queries, mixed with point updates):");
        System.out.println("  sum mismatches = " + sumMismatch + ", min = " + minMismatch
            + ", max = " + maxMismatch + ", gcd = " + gcdMismatch);

        System.out.println();
        int[] sizes = {1000, 10000, 1 << 14, 100000};
        for (int n : sizes) {
            int[] big = new int[n];
            for (int i = 0; i < n; i++) big[i] = random.nextInt(100);
            GenericSegmentTree big2 = new GenericSegmentTree(big, Integer::sum, 0);

            int worst = 0;
            long totalVisits = 0;
            int queries = 2000;
            for (int q = 0; q < queries; q++) {
                int l = random.nextInt(n);
                int r = l + random.nextInt(n - l);
                big2.visits = 0;
                big2.query(l, r);
                worst = Math.max(worst, big2.visits);
                totalVisits += big2.visits;
            }
            int levels = (int) Math.ceil(Math.log(n) / Math.log(2)) + 1;
            System.out.println("n = " + n + ": " + queries + " random range queries visited at most " + worst
                + " nodes (average " + (totalVisits / queries) + "), bound 4 x levels = " + (4 * levels)
                + ", naive scan would average ~" + (n / 3) + " elements");
        }

        System.out.println();
        int lazyTrials = 300;
        int lazyMismatches = 0;
        int lazyQueries = 0;
        for (int t = 0; t < lazyTrials; t++) {
            int n = 1 + random.nextInt(20);
            long[] naive = new long[n];
            int[] start = new int[n];
            for (int i = 0; i < n; i++) {
                start[i] = random.nextInt(21) - 10;
                naive[i] = start[i];
            }
            LazySegmentTree lazy = new LazySegmentTree(start);

            for (int op = 0; op < 60; op++) {
                int l = random.nextInt(n);
                int r = l + random.nextInt(n - l);
                if (random.nextBoolean()) {
                    int delta = random.nextInt(11) - 5;
                    for (int i = l; i <= r; i++) naive[i] += delta;
                    lazy.rangeAdd(l, r, delta);
                } else {
                    lazyQueries++;
                    long expected = 0;
                    for (int i = l; i <= r; i++) expected += naive[i];
                    if (lazy.rangeSum(l, r) != expected) lazyMismatches++;
                }
            }
        }
        System.out.println("lazy range-add / range-sum tree vs naive over " + lazyTrials + " random arrays ("
            + lazyQueries + " queries): " + lazyMismatches + " mismatches");

        int n = 100000;
        LazySegmentTree bigLazy = new LazySegmentTree(new int[n]);
        long naiveTouches = 0;
        int rangeOps = 2000;
        bigLazy.visits = 0;
        for (int op = 0; op < rangeOps; op++) {
            int l = random.nextInt(n);
            int r = l + random.nextInt(n - l);
            bigLazy.rangeAdd(l, r, 1);
            naiveTouches += r - l + 1; // a plain loop would have to modify every element in the range
        }
        System.out.println("n = " + n + ", " + rangeOps + " range-adds: lazy tree visited " + bigLazy.visits
            + " nodes in total vs " + naiveTouches + " element writes for a plain loop");
    }

    static int gcd(int a, int b) {
        while (b != 0) {
            int t = a % b;
            a = b;
            b = t;
        }
        return a;
    }
}

class SegmentTree {
    private final int n;
    private final long[] tree;

    SegmentTree(int[] data) {
        n = data.length;
        tree = new long[4 * Math.max(1, n)];
        if (n > 0) build(data, 1, 0, n - 1);
    }

    private void build(int[] data, int node, int l, int r) {
        if (l == r) {
            tree[node] = data[l];
            return;
        }
        int mid = (l + r) / 2;
        build(data, 2 * node, l, mid);
        build(data, 2 * node + 1, mid + 1, r);
        tree[node] = tree[2 * node] + tree[2 * node + 1];
    }

    long query(int ql, int qr) {
        return query(1, 0, n - 1, ql, qr);
    }

    private long query(int node, int l, int r, int ql, int qr) {
        if (qr < l || r < ql) return 0;
        if (ql <= l && r <= qr) return tree[node];
        int mid = (l + r) / 2;
        return query(2 * node, l, mid, ql, qr) + query(2 * node + 1, mid + 1, r, ql, qr);
    }

    void update(int idx, int value) {
        update(1, 0, n - 1, idx, value);
    }

    private void update(int node, int l, int r, int idx, int value) {
        if (l == r) {
            tree[node] = value;
            return;
        }
        int mid = (l + r) / 2;
        if (idx <= mid) update(2 * node, l, mid, idx, value);
        else update(2 * node + 1, mid + 1, r, idx, value);
        tree[node] = tree[2 * node] + tree[2 * node + 1];
    }
}

class GenericSegmentTree {
    private final int n;
    private final int[] tree;
    private final IntBinaryOperator combine;
    private final int identity;
    int visits; // counts nodes touched by query(), to show the O(log n) bound directly

    GenericSegmentTree(int[] data, IntBinaryOperator combine, int identity) {
        this.n = data.length;
        this.combine = combine;
        this.identity = identity;
        this.tree = new int[4 * Math.max(1, n)];
        if (n > 0) build(data, 1, 0, n - 1);
    }

    private void build(int[] data, int node, int l, int r) {
        if (l == r) {
            tree[node] = data[l];
            return;
        }
        int mid = (l + r) / 2;
        build(data, 2 * node, l, mid);
        build(data, 2 * node + 1, mid + 1, r);
        tree[node] = combine.applyAsInt(tree[2 * node], tree[2 * node + 1]);
    }

    int query(int ql, int qr) {
        return query(1, 0, n - 1, ql, qr);
    }

    private int query(int node, int l, int r, int ql, int qr) {
        visits++;
        if (qr < l || r < ql) return identity;
        if (ql <= l && r <= qr) return tree[node];
        int mid = (l + r) / 2;
        return combine.applyAsInt(query(2 * node, l, mid, ql, qr), query(2 * node + 1, mid + 1, r, ql, qr));
    }

    void update(int idx, int value) {
        update(1, 0, n - 1, idx, value);
    }

    private void update(int node, int l, int r, int idx, int value) {
        if (l == r) {
            tree[node] = value;
            return;
        }
        int mid = (l + r) / 2;
        if (idx <= mid) update(2 * node, l, mid, idx, value);
        else update(2 * node + 1, mid + 1, r, idx, value);
        tree[node] = combine.applyAsInt(tree[2 * node], tree[2 * node + 1]);
    }
}

class LazySegmentTree {
    private final int n;
    private final long[] sum;
    private final long[] lazy;
    long visits; // counts nodes touched by rangeAdd() and rangeSum()

    LazySegmentTree(int[] data) {
        n = data.length;
        sum = new long[4 * Math.max(1, n)];
        lazy = new long[4 * Math.max(1, n)];
        if (n > 0) build(data, 1, 0, n - 1);
    }

    private void build(int[] data, int node, int l, int r) {
        if (l == r) {
            sum[node] = data[l];
            return;
        }
        int mid = (l + r) / 2;
        build(data, 2 * node, l, mid);
        build(data, 2 * node + 1, mid + 1, r);
        sum[node] = sum[2 * node] + sum[2 * node + 1];
    }

    private void apply(int node, int l, int r, long delta) {
        sum[node] += delta * (r - l + 1);
        lazy[node] += delta;
    }

    private void push(int node, int l, int r) {
        if (lazy[node] == 0) return;
        int mid = (l + r) / 2;
        apply(2 * node, l, mid, lazy[node]);
        apply(2 * node + 1, mid + 1, r, lazy[node]);
        lazy[node] = 0;
    }

    void rangeAdd(int ql, int qr, long delta) {
        rangeAdd(1, 0, n - 1, ql, qr, delta);
    }

    private void rangeAdd(int node, int l, int r, int ql, int qr, long delta) {
        visits++;
        if (qr < l || r < ql) return;
        if (ql <= l && r <= qr) {
            apply(node, l, r, delta);
            return;
        }
        push(node, l, r);
        int mid = (l + r) / 2;
        rangeAdd(2 * node, l, mid, ql, qr, delta);
        rangeAdd(2 * node + 1, mid + 1, r, ql, qr, delta);
        sum[node] = sum[2 * node] + sum[2 * node + 1];
    }

    long rangeSum(int ql, int qr) {
        return rangeSum(1, 0, n - 1, ql, qr);
    }

    private long rangeSum(int node, int l, int r, int ql, int qr) {
        visits++;
        if (qr < l || r < ql) return 0;
        if (ql <= l && r <= qr) return sum[node];
        push(node, l, r);
        int mid = (l + r) / 2;
        return rangeSum(2 * node, l, mid, ql, qr) + rangeSum(2 * node + 1, mid + 1, r, ql, qr);
    }
}
