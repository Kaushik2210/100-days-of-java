import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class KruskalDemo {

    public static void main(String[] args) {
        // the CLRS textbook graph: vertices a..i = 0..8
        String names = "abcdefghi";
        int[][] edges = {
            {0, 1, 4}, {0, 7, 8}, {1, 2, 8}, {1, 7, 11}, {2, 3, 7}, {2, 5, 4}, {2, 8, 2},
            {3, 4, 9}, {3, 5, 14}, {4, 5, 10}, {5, 6, 2}, {6, 7, 1}, {6, 8, 6}, {7, 8, 7}
        };

        List<int[]> chosen = new ArrayList<>();
        int total = kruskal(9, edges, chosen);
        System.out.println("MST total weight = " + total + " (known textbook answer: 37)");
        System.out.println("edges kept (" + chosen.size() + ", expected V - 1 = 8):");
        for (int[] edge : chosen) {
            System.out.println("  " + names.charAt(edge[0]) + " - " + names.charAt(edge[1]) + "  weight " + edge[2]);
        }

        System.out.println();
        // two separate components: {0,1,2} and {3,4}; vertex 5 is isolated
        int[][] disconnected = {{0, 1, 5}, {1, 2, 3}, {0, 2, 4}, {3, 4, 7}};
        List<int[]> forest = new ArrayList<>();
        int forestWeight = kruskal(6, disconnected, forest);
        System.out.println("disconnected graph (6 vertices, 3 components): kept " + forest.size()
            + " edges (expected V - C = 3), total weight " + forestWeight + " (expected 3 + 4 + 7 = 14)");

        System.out.println();
        Random random = new Random(83); // fixed seed so the run is repeatable
        int trials = 500;
        int weightMismatches = 0;
        int notSpanningTree = 0;
        int tieGraphs = 0;
        int orderDependent = 0;
        for (int t = 0; t < trials; t++) {
            int n = 2 + random.nextInt(5);
            List<int[]> edgeList = new ArrayList<>();
            for (int v = 1; v < n; v++) {
                edgeList.add(new int[]{v, random.nextInt(v), 1 + random.nextInt(10)}); // keeps the graph connected
            }
            int extras = random.nextInt(Math.min(9, 14 - edgeList.size()) + 1);
            for (int i = 0; i < extras; i++) {
                edgeList.add(new int[]{random.nextInt(n), random.nextInt(n), 1 + random.nextInt(10)}); // may be a self-loop or parallel
            }
            int[][] graphEdges = edgeList.toArray(new int[0][]);

            List<int[]> mst = new ArrayList<>();
            int weight = kruskal(n, graphEdges, mst);

            if (weight != bruteForceMstWeight(n, graphEdges)) weightMismatches++;
            if (mst.size() != n - 1 || !spansAll(n, mst)) notSpanningTree++;

            boolean hasTie = false;
            for (int i = 0; i < graphEdges.length && !hasTie; i++) {
                for (int j = i + 1; j < graphEdges.length; j++) {
                    if (graphEdges[i][2] == graphEdges[j][2]) { hasTie = true; break; }
                }
            }
            if (hasTie) tieGraphs++;

            List<int[]> shuffled = new ArrayList<>(edgeList);
            Collections.shuffle(shuffled, random);
            if (kruskal(n, shuffled.toArray(new int[0][]), new ArrayList<>()) != weight) orderDependent++;
        }
        System.out.println("Kruskal vs exhaustive search over every spanning tree, " + trials + " random connected graphs:");
        System.out.println("  MST weight mismatches = " + weightMismatches);
        System.out.println("  results that were not a V-1 edge spanning tree = " + notSpanningTree);
        System.out.println("  total weight changed after shuffling edge order = " + orderDependent
            + " (" + tieGraphs + " graphs contained tied weights)");
    }

    // true when the chosen edges reach every vertex, checked with an independent DFS (Day 70)
    static boolean spansAll(int n, List<int[]> chosen) {
        List<List<Integer>> adjacency = new ArrayList<>();
        for (int i = 0; i < n; i++) adjacency.add(new ArrayList<>());
        for (int[] edge : chosen) {
            adjacency.get(edge[0]).add(edge[1]);
            adjacency.get(edge[1]).add(edge[0]);
        }
        boolean[] seen = new boolean[n];
        java.util.ArrayDeque<Integer> stack = new java.util.ArrayDeque<>();
        stack.push(0);
        seen[0] = true;
        int reached = 1;
        while (!stack.isEmpty()) {
            int u = stack.pop();
            for (int v : adjacency.get(u)) {
                if (!seen[v]) {
                    seen[v] = true;
                    reached++;
                    stack.push(v);
                }
            }
        }
        return reached == n;
    }

    // tries every subset of exactly n-1 edges and keeps the cheapest one that spans all vertices
    static int bruteForceMstWeight(int n, int[][] edges) {
        int best = Integer.MAX_VALUE;
        for (int mask = 0; mask < (1 << edges.length); mask++) {
            if (Integer.bitCount(mask) != n - 1) continue;
            List<int[]> subset = new ArrayList<>();
            int weight = 0;
            for (int i = 0; i < edges.length; i++) {
                if ((mask & (1 << i)) != 0) {
                    subset.add(edges[i]);
                    weight += edges[i][2];
                }
            }
            if (weight < best && spansAll(n, subset)) best = weight;
        }
        return best;
    }

    static int kruskal(int n, int[][] edges, List<int[]> chosen) {
        int[][] sorted = edges.clone();
        Arrays.sort(sorted, (a, b) -> Integer.compare(a[2], b[2]));

        UnionFind uf = new UnionFind(n);
        int total = 0;
        for (int[] edge : sorted) {
            if (uf.union(edge[0], edge[1])) {
                chosen.add(edge);
                total += edge[2];
                if (chosen.size() == n - 1) break;
            }
        }
        return total;
    }
}

class UnionFind {
    private int[] parent;
    private int[] size;

    UnionFind(int n) {
        parent = new int[n];
        size = new int[n];
        for (int i = 0; i < n; i++) {
            parent[i] = i;
            size[i] = 1;
        }
    }

    int find(int x) {
        if (parent[x] != x) {
            parent[x] = find(parent[x]);
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
        return true;
    }
}
