import java.util.Arrays;

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
    }
}

class FenwickTree {
    private final int n;
    private final long[] tree;

    FenwickTree(int n) {
        this.n = n;
        this.tree = new long[n + 1]; // index 0 is unused
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
}
