import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Random;

public class PrimDemo {

    static final int NO_EDGE = Integer.MIN_VALUE;

    public static void main(String[] args) {
        // the CLRS textbook graph again: vertices a..i = 0..8, the same one Kruskal used on Day 83
        String names = "abcdefghi";
        int[][] edges = {
            {0, 1, 4}, {0, 7, 8}, {1, 2, 8}, {1, 7, 11}, {2, 3, 7}, {2, 5, 4}, {2, 8, 2},
            {3, 4, 9}, {3, 5, 14}, {4, 5, 10}, {5, 6, 2}, {6, 7, 1}, {6, 8, 6}, {7, 8, 7}
        };
        List<List<int[]>> graph = buildGraph(9, edges);

        List<int[]> chosen = new ArrayList<>();
        int total = prim(graph, 0, chosen);
        System.out.println("Prim from vertex a: MST total weight = " + total + " (Kruskal on Day 83 also found 37)");
        System.out.println("edges added in the order the tree grew (" + chosen.size() + ", expected V - 1 = 8):");
        for (int[] edge : chosen) {
            System.out.println("  " + names.charAt(edge[0]) + " - " + names.charAt(edge[1]) + "  weight " + edge[2]);
        }
        System.out.println("dense O(V^2) variant on the same graph = " + primDense(buildMatrix(9, edges), 9));

        System.out.println();
        // Prim and Dijkstra differ in what they key on, so they can build different trees
        int[][] triangle = {{0, 1, 2}, {1, 2, 2}, {0, 2, 3}};
        List<List<int[]>> triangleGraph = buildGraph(3, triangle);
        int primWeight = prim(triangleGraph, 0, new ArrayList<>());
        int dijkstraTreeWeight = dijkstraTreeWeight(triangleGraph, 0);
        System.out.println("triangle with edges 0-1 (2), 1-2 (2), 0-2 (3):");
        System.out.println("  Prim's MST total weight             = " + primWeight + " (expected 4: uses 0-1 and 1-2)");
        System.out.println("  Dijkstra's shortest-path tree total = " + dijkstraTreeWeight + " (expected 5: uses 0-1 and 0-2)");

        System.out.println();
        // two components {0,1,2} and {3,4}, plus isolated vertex 5
        int[][] disconnected = {{0, 1, 5}, {1, 2, 3}, {0, 2, 4}, {3, 4, 7}};
        List<List<int[]>> disconnectedGraph = buildGraph(6, disconnected);
        List<int[]> singleRun = new ArrayList<>();
        int singleWeight = prim(disconnectedGraph, 0, singleRun);
        List<int[]> forest = new ArrayList<>();
        int forestWeight = primForest(disconnectedGraph, forest);
        System.out.println("disconnected graph, single Prim run from 0: " + singleRun.size() + " edges, weight " + singleWeight
            + " (covers only 0's component)");
        System.out.println("disconnected graph, Prim restarted from every unvisited vertex: " + forest.size()
            + " edges, weight " + forestWeight + " (expected V - C = 3 edges, weight 14)");

        System.out.println();
        Random random = new Random(84); // fixed seed so the run is repeatable
        int trials = 500;
        int heapVsBrute = 0;
        int denseVsBrute = 0;
        int kruskalVsBrute = 0;
        int startDependent = 0;
        for (int t = 0; t < trials; t++) {
            int n = 2 + random.nextInt(5);
            List<int[]> edgeList = new ArrayList<>();
            for (int v = 1; v < n; v++) {
                edgeList.add(new int[]{v, random.nextInt(v), 1 + random.nextInt(10)});
            }
            int extras = random.nextInt(Math.min(9, 14 - edgeList.size()) + 1);
            for (int i = 0; i < extras; i++) {
                edgeList.add(new int[]{random.nextInt(n), random.nextInt(n), 1 + random.nextInt(10)}); // self-loops and parallel edges
            }
            int[][] graphEdges = edgeList.toArray(new int[0][]);
            List<List<int[]>> g = buildGraph(n, graphEdges);

            int expected = bruteForceMstWeight(n, graphEdges);
            int start = random.nextInt(n);
            if (prim(g, start, new ArrayList<>()) != expected) heapVsBrute++;
            if (primDense(buildMatrix(n, graphEdges), n) != expected) denseVsBrute++;
            if (kruskalWeight(n, graphEdges) != expected) kruskalVsBrute++;
            for (int s = 0; s < n; s++) {
                if (prim(g, s, new ArrayList<>()) != expected) startDependent++;
            }
        }
        System.out.println("Prim vs exhaustive search over every spanning tree, " + trials + " random connected graphs:");
        System.out.println("  heap-based Prim mismatches   = " + heapVsBrute);
        System.out.println("  dense O(V^2) Prim mismatches = " + denseVsBrute);
        System.out.println("  independent Kruskal mismatches = " + kruskalVsBrute);
        System.out.println("  runs where the starting vertex changed the weight = " + startDependent);

        int forestMismatches = 0;
        int forestEdgeCountWrong = 0;
        int disconnectedTrials = 400;
        int trulyDisconnected = 0;
        for (int t = 0; t < disconnectedTrials; t++) {
            int n = 1 + random.nextInt(9);
            List<int[]> edgeList = new ArrayList<>();
            int count = random.nextInt(n + 2); // few edges, so many graphs fall apart into several components
            for (int i = 0; i < count; i++) {
                edgeList.add(new int[]{random.nextInt(n), random.nextInt(n), 1 + random.nextInt(10)});
            }
            int[][] graphEdges = edgeList.toArray(new int[0][]);
            List<int[]> chosenForest = new ArrayList<>();
            int w = primForest(buildGraph(n, graphEdges), chosenForest);

            if (w != kruskalWeight(n, graphEdges)) forestMismatches++;
            int components = countComponents(n, graphEdges);
            if (components > 1) trulyDisconnected++;
            if (chosenForest.size() != n - components) forestEdgeCountWrong++;
        }
        System.out.println("restarted Prim vs Kruskal's forest weight over " + disconnectedTrials + " random graphs ("
            + trulyDisconnected + " genuinely disconnected):");
        System.out.println("  forest weight mismatches = " + forestMismatches);
        System.out.println("  edge counts that were not V - C = " + forestEdgeCountWrong);
    }

    static List<List<int[]>> buildGraph(int n, int[][] edges) {
        List<List<int[]>> graph = new ArrayList<>();
        for (int i = 0; i < n; i++) graph.add(new ArrayList<>());
        for (int[] edge : edges) {
            graph.get(edge[0]).add(new int[]{edge[1], edge[2]});
            graph.get(edge[1]).add(new int[]{edge[0], edge[2]});
        }
        return graph;
    }

    static int[][] buildMatrix(int n, int[][] edges) {
        int[][] weights = new int[n][n];
        for (int[] row : weights) Arrays.fill(row, NO_EDGE);
        for (int[] edge : edges) {
            if (edge[0] == edge[1]) continue; // a self-loop can never be part of a spanning tree
            int current = weights[edge[0]][edge[1]];
            int w = current == NO_EDGE ? edge[2] : Math.min(current, edge[2]); // parallel edges: keep the cheapest
            weights[edge[0]][edge[1]] = w;
            weights[edge[1]][edge[0]] = w;
        }
        return weights;
    }

    static int prim(List<List<int[]>> graph, int start, List<int[]> chosen) {
        return primFrom(graph, start, new boolean[graph.size()], chosen);
    }

    static int primFrom(List<List<int[]>> graph, int start, boolean[] inTree, List<int[]> chosen) {
        PriorityQueue<int[]> queue = new PriorityQueue<>((a, b) -> Integer.compare(a[0], b[0]));
        queue.add(new int[]{0, start, -1});

        int total = 0;
        while (!queue.isEmpty()) {
            int[] top = queue.poll();
            int weight = top[0];
            int u = top[1];
            if (inTree[u]) continue;

            inTree[u] = true;
            total += weight;
            if (top[2] != -1) chosen.add(new int[]{top[2], u, weight});

            for (int[] edge : graph.get(u)) {
                if (!inTree[edge[0]]) {
                    queue.add(new int[]{edge[1], edge[0], u});
                }
            }
        }
        return total;
    }

    static int primForest(List<List<int[]>> graph, List<int[]> chosen) {
        boolean[] inTree = new boolean[graph.size()];
        int total = 0;
        for (int start = 0; start < graph.size(); start++) {
            if (!inTree[start]) total += primFrom(graph, start, inTree, chosen);
        }
        return total;
    }

    static int primDense(int[][] weights, int n) {
        int[] best = new int[n];
        boolean[] inTree = new boolean[n];
        Arrays.fill(best, Integer.MAX_VALUE);
        best[0] = 0;

        int total = 0;
        for (int step = 0; step < n; step++) {
            int u = -1;
            for (int v = 0; v < n; v++) {
                if (!inTree[v] && (u == -1 || best[v] < best[u])) u = v;
            }
            if (best[u] == Integer.MAX_VALUE) break;
            inTree[u] = true;
            total += best[u];

            for (int v = 0; v < n; v++) {
                if (!inTree[v] && weights[u][v] != NO_EDGE && weights[u][v] < best[v]) {
                    best[v] = weights[u][v];
                }
            }
        }
        return total;
    }

    // Dijkstra, used only to show its tree differs from the MST; returns the summed weight of the edges it uses
    static int dijkstraTreeWeight(List<List<int[]>> graph, int source) {
        int n = graph.size();
        int[] dist = new int[n];
        int[] parentWeight = new int[n];
        Arrays.fill(dist, Integer.MAX_VALUE);
        boolean[] done = new boolean[n];
        dist[source] = 0;

        PriorityQueue<int[]> queue = new PriorityQueue<>((a, b) -> Integer.compare(a[0], b[0]));
        queue.add(new int[]{0, source});
        while (!queue.isEmpty()) {
            int u = queue.poll()[1];
            if (done[u]) continue;
            done[u] = true;
            for (int[] edge : graph.get(u)) {
                if (!done[edge[0]] && dist[u] + edge[1] < dist[edge[0]]) {
                    dist[edge[0]] = dist[u] + edge[1];
                    parentWeight[edge[0]] = edge[1];
                    queue.add(new int[]{dist[edge[0]], edge[0]});
                }
            }
        }
        int total = 0;
        for (int v = 0; v < n; v++) {
            if (v != source && dist[v] != Integer.MAX_VALUE) total += parentWeight[v];
        }
        return total;
    }

    // Kruskal's total weight, written independently here as a cross-check (no early exit, so it works on forests too)
    static int kruskalWeight(int n, int[][] edges) {
        int[][] sorted = edges.clone();
        Arrays.sort(sorted, (a, b) -> Integer.compare(a[2], b[2]));
        int[] parent = new int[n];
        for (int i = 0; i < n; i++) parent[i] = i;

        int total = 0;
        for (int[] edge : sorted) {
            int a = root(parent, edge[0]);
            int b = root(parent, edge[1]);
            if (a != b) {
                parent[a] = b;
                total += edge[2];
            }
        }
        return total;
    }

    static int root(int[] parent, int x) {
        while (parent[x] != x) x = parent[x];
        return x;
    }

    static int countComponents(int n, int[][] edges) {
        List<List<Integer>> adjacency = new ArrayList<>();
        for (int i = 0; i < n; i++) adjacency.add(new ArrayList<>());
        for (int[] edge : edges) {
            adjacency.get(edge[0]).add(edge[1]);
            adjacency.get(edge[1]).add(edge[0]);
        }
        boolean[] seen = new boolean[n];
        int components = 0;
        for (int s = 0; s < n; s++) {
            if (seen[s]) continue;
            components++;
            ArrayDeque<Integer> stack = new ArrayDeque<>();
            stack.push(s);
            seen[s] = true;
            while (!stack.isEmpty()) {
                int u = stack.pop();
                for (int v : adjacency.get(u)) {
                    if (!seen[v]) {
                        seen[v] = true;
                        stack.push(v);
                    }
                }
            }
        }
        return components;
    }

    static boolean spansAll(int n, List<int[]> chosen) {
        List<int[]> asEdges = new ArrayList<>(chosen);
        return countComponents(n, asEdges.toArray(new int[0][])) == 1;
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
}
