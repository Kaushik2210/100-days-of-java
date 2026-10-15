import java.util.Arrays;

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
        if (qr < l || r < ql) return 0;            // this node's segment is entirely outside the query
        if (ql <= l && r <= qr) return tree[node]; // entirely inside: its stored sum is exactly what is needed
        int mid = (l + r) / 2;                     // partial overlap: ask both children
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
