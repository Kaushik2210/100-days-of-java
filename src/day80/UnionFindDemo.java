import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;

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

        UnionFind optimized = new UnionFind(n);
        for (int i = 0; i < n - 1; i++) {
            optimized.union(i, i + 1); // the exact same chain of unions
        }
        System.out.println("optimized union-find, same " + (n - 1) + " unions:");
        System.out.println("  tallest tree = " + optimized.maxDepth()
            + " (union by size hangs each lone new item under the big group, so the chain never forms)");
        System.out.println("  components = " + optimized.componentCount() + " (one group containing all " + n + " items)");

        System.out.println();
        int k = 12;
        int tournamentSize = 1 << k; // 4096 items, merged in equal-size pairs: the worst case for union by size
        UnionFind tournament = new UnionFind(tournamentSize);
        for (int step = 1; step < tournamentSize; step *= 2) {
            for (int i = 0; i < tournamentSize; i += 2 * step) {
                tournament.union(i, i + step); // two groups of equal size merge at every level
            }
        }
        System.out.println("equal-size merging of " + tournamentSize + " items (log2 = " + k + "):");
        System.out.println("  tallest tree before any find = " + tournament.maxDepth()
            + " (union by size alone reaches exactly log2(n), its proven upper bound)");
        for (int i = 0; i < tournamentSize; i++) tournament.find(i);
        System.out.println("  tallest tree after find() on every item = " + tournament.maxDepth()
            + " (path compression flattened everything to one step from the root)");

        System.out.println();
        Random random = new Random(80); // fixed seed so the run is repeatable
        int trials = 500;
        int connectivityMismatches = 0;
        int componentMismatches = 0;
        int cycleMismatches = 0;
        int graphsWithCycles = 0;
        for (int t = 0; t < trials; t++) {
            int vertices = 1 + random.nextInt(25);
            int edgeCount = random.nextInt(2 * vertices);
            List<int[]> edges = new ArrayList<>();
            for (int e = 0; e < edgeCount; e++) {
                edges.add(new int[]{random.nextInt(vertices), random.nextInt(vertices)}); // self-loops and duplicates allowed
            }

            UnionFind dsu = new UnionFind(vertices);
            boolean cycleSeen = false;
            for (int[] edge : edges) {
                if (!dsu.union(edge[0], edge[1])) cycleSeen = true; // endpoints were already connected
            }

            int[] label = bfsComponents(vertices, edges);
            int bfsComponentCount = 0;
            for (int l : label) bfsComponentCount = Math.max(bfsComponentCount, l + 1);

            for (int a = 0; a < vertices; a++) {
                for (int b = 0; b < vertices; b++) {
                    if (dsu.connected(a, b) != (label[a] == label[b])) connectivityMismatches++;
                }
            }
            if (dsu.componentCount() != bfsComponentCount) componentMismatches++;

            boolean cycleByCounting = edges.size() > vertices - bfsComponentCount; // a forest has exactly n - components edges
            if (cycleSeen != cycleByCounting) cycleMismatches++;
            if (cycleSeen) graphsWithCycles++;
        }
        System.out.println("union-find vs BFS over " + trials + " random graphs (incl. self-loops and duplicate edges):");
        System.out.println("  pairwise connectivity mismatches = " + connectivityMismatches);
        System.out.println("  component count mismatches       = " + componentMismatches);
        System.out.println("  cycle detection mismatches       = " + cycleMismatches
            + " (" + graphsWithCycles + " of the " + trials + " graphs actually contained a cycle)");
    }

    // labels each vertex with a component id using BFS (Day 70), as an independent answer to compare against
    static int[] bfsComponents(int vertices, List<int[]> edges) {
        List<List<Integer>> adjacency = new ArrayList<>();
        for (int i = 0; i < vertices; i++) adjacency.add(new ArrayList<>());
        for (int[] edge : edges) {
            adjacency.get(edge[0]).add(edge[1]);
            adjacency.get(edge[1]).add(edge[0]);
        }

        int[] label = new int[vertices];
        Arrays.fill(label, -1);
        int next = 0;
        for (int start = 0; start < vertices; start++) {
            if (label[start] != -1) continue;
            Queue<Integer> queue = new LinkedList<>();
            queue.add(start);
            label[start] = next;
            while (!queue.isEmpty()) {
                int current = queue.poll();
                for (int neighbor : adjacency.get(current)) {
                    if (label[neighbor] == -1) {
                        label[neighbor] = next;
                        queue.add(neighbor);
                    }
                }
            }
            next++;
        }
        return label;
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

class UnionFind {
    private int[] parent;
    private int[] size;
    private int components;

    UnionFind(int n) {
        parent = new int[n];
        size = new int[n];
        components = n;
        for (int i = 0; i < n; i++) {
            parent[i] = i;
            size[i] = 1;
        }
    }

    int find(int x) {
        if (parent[x] != x) {
            parent[x] = find(parent[x]); // path compression: point x straight at the root
        }
        return parent[x];
    }

    boolean union(int a, int b) {
        int rootA = find(a);
        int rootB = find(b);
        if (rootA == rootB) return false;

        if (size[rootA] < size[rootB]) {
            int temp = rootA;
            rootA = rootB;
            rootB = temp;
        }
        parent[rootB] = rootA;
        size[rootA] += size[rootB];
        components--;
        return true;
    }

    boolean connected(int a, int b) {
        return find(a) == find(b);
    }

    int componentCount() {
        return components;
    }

    // measures tree height without compressing anything, so it reports the structure as it currently stands
    int maxDepth() {
        int max = 0;
        for (int i = 0; i < parent.length; i++) {
            int steps = 0;
            int x = i;
            while (parent[x] != x) {
                x = parent[x];
                steps++;
            }
            max = Math.max(max, steps);
        }
        return max;
    }
}
