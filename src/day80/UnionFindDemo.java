public class UnionFindDemo {

    public static void main(String[] args) {
        NaiveUnionFind uf = new NaiveUnionFind(6);
        uf.union(0, 1);
        uf.union(1, 2);
        uf.union(3, 4);

        System.out.println("connected(0, 2) = " + uf.connected(0, 2) + " (joined through 1)");
        System.out.println("connected(0, 3) = " + uf.connected(0, 3) + " (different groups)");
        System.out.println("connected(3, 4) = " + uf.connected(3, 4));
        System.out.println("connected(5, 5) = " + uf.connected(5, 5) + " (an item is always connected to itself)");

        uf.union(2, 3); // merges the {0,1,2} group with the {3,4} group
        System.out.println("after union(2, 3): connected(0, 4) = " + uf.connected(0, 4));

        System.out.println();
        int n = 5000;
        NaiveUnionFind chain = new NaiveUnionFind(n);
        for (int i = 0; i < n - 1; i++) {
            chain.union(i, i + 1); // union the items in a straight chain
        }
        System.out.println("naive union-find after chaining " + n + " items:");
        System.out.println("  steps for find(0) to reach its root = " + chain.depth(0) + " (expected " + (n - 1) + ")");
    }
}

class NaiveUnionFind {
    private int[] parent;

    NaiveUnionFind(int n) {
        parent = new int[n];
        for (int i = 0; i < n; i++) parent[i] = i;
    }

    int find(int x) {
        while (parent[x] != x) {
            x = parent[x];
        }
        return x;
    }

    void union(int a, int b) {
        parent[find(a)] = find(b);
    }

    boolean connected(int a, int b) {
        return find(a) == find(b);
    }

    // number of parent links followed from x up to its root, used to measure how tall the trees get
    int depth(int x) {
        int steps = 0;
        while (parent[x] != x) {
            x = parent[x];
            steps++;
        }
        return steps;
    }
}
