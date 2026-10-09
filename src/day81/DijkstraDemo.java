import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Random;

public class DijkstraDemo {

    public static void main(String[] args) {
        // the classic textbook graph, undirected, vertices 0..5 plus an isolated vertex 6
        List<List<int[]>> graph = emptyGraph(7);
        addUndirectedEdge(graph, 0, 1, 7);
        addUndirectedEdge(graph, 0, 2, 9);
        addUndirectedEdge(graph, 0, 5, 14);
        addUndirectedEdge(graph, 1, 2, 10);
        addUndirectedEdge(graph, 1, 3, 15);
        addUndirectedEdge(graph, 2, 3, 11);
        addUndirectedEdge(graph, 2, 5, 2);
        addUndirectedEdge(graph, 3, 4, 6);
        addUndirectedEdge(graph, 4, 5, 9);

        int[] dist = dijkstra(graph, 0);
        System.out.println("shortest distances from vertex 0:");
        for (int v = 0; v < dist.length; v++) {
            String shown = dist[v] == Integer.MAX_VALUE ? "unreachable" : String.valueOf(dist[v]);
            System.out.println("  to " + v + " = " + shown);
        }
        System.out.println("expected for the textbook graph: [0, 7, 9, 20, 20, 11] and vertex 6 unreachable");

        int[] parent = new int[graph.size()];
        dijkstra(graph, 0, parent);
        System.out.println("route 0 -> 4: " + pathTo(4, parent, 0) + " (cost " + dist[4] + ")");
        System.out.println("route 0 -> 6: " + pathTo(6, parent, 0) + " (empty: unreachable)");

        System.out.println();
        Random random = new Random(81); // fixed seed so the run is repeatable
        int trials = 400;
        int distanceMismatches = 0;
        int badPaths = 0;
        int pairsChecked = 0;
        for (int t = 0; t < trials; t++) {
            int n = 1 + random.nextInt(10);
            int[][] weights = new int[n][n];
            for (int u = 0; u < n; u++) {
                for (int v = 0; v < n; v++) {
                    weights[u][v] = (u != v && random.nextInt(100) < 35) ? random.nextInt(21) : NO_EDGE; // weights 0..20
                }
            }
            List<List<int[]>> g = graphFromMatrix(n, weights);
            int[][] oracle = floydWarshall(n, weights);

            for (int source = 0; source < n; source++) {
                int[] par = new int[n];
                int[] d = dijkstra(g, source, par);
                for (int target = 0; target < n; target++) {
                    pairsChecked++;
                    if (d[target] != oracle[source][target]) distanceMismatches++;

                    if (d[target] != Integer.MAX_VALUE) {
                        List<Integer> path = pathTo(target, par, source);
                        int cost = 0;
                        boolean valid = !path.isEmpty() && path.get(0) == source && path.get(path.size() - 1) == target;
                        for (int i = 0; valid && i + 1 < path.size(); i++) {
                            int w = weights[path.get(i)][path.get(i + 1)];
                            if (w == NO_EDGE) valid = false; // path uses an edge that does not exist
                            else cost += w;
                        }
                        if (!valid || cost != d[target]) badPaths++;
                    }
                }
            }
        }
        System.out.println("Dijkstra vs Floyd-Warshall over " + trials + " random directed graphs (" + pairsChecked + " source/target pairs):");
        System.out.println("  distance mismatches = " + distanceMismatches);
        System.out.println("  recovered paths that were invalid or had the wrong cost = " + badPaths);

        System.out.println();
        System.out.println("Negative edge: 0->1 (2), 0->2 (3), 2->1 (-2). True shortest 0->1 is 0->2->1 = 1.");
        int[][] negative = new int[3][3];
        for (int[] row : negative) Arrays.fill(row, NO_EDGE);
        negative[0][1] = 2;
        negative[0][2] = 3;
        negative[2][1] = -2;
        int[] dijkstraNegative = dijkstra(graphFromMatrix(3, negative), 0);
        int[][] floydNegative = floydWarshall(3, negative);
        System.out.println("  Dijkstra says dist[1] = " + dijkstraNegative[1] + " (wrong)");
        System.out.println("  Floyd-Warshall says dist[1] = " + floydNegative[0][1] + " (correct)");
    }

    static List<List<int[]>> emptyGraph(int n) {
        List<List<int[]>> graph = new ArrayList<>();
        for (int i = 0; i < n; i++) graph.add(new ArrayList<>());
        return graph;
    }

    static void addUndirectedEdge(List<List<int[]>> graph, int a, int b, int weight) {
        graph.get(a).add(new int[]{b, weight});
        graph.get(b).add(new int[]{a, weight});
    }

    static int[] dijkstra(List<List<int[]>> graph, int source) {
        return dijkstra(graph, source, new int[graph.size()]);
    }

    // same algorithm, but also fills parent[] so the actual routes can be recovered afterwards
    static int[] dijkstra(List<List<int[]>> graph, int source, int[] parent) {
        int n = graph.size();
        int[] dist = new int[n];
        Arrays.fill(dist, Integer.MAX_VALUE);
        Arrays.fill(parent, -1);
        boolean[] finalized = new boolean[n];
        dist[source] = 0;

        PriorityQueue<int[]> queue = new PriorityQueue<>((a, b) -> Integer.compare(a[0], b[0]));
        queue.add(new int[]{0, source});

        while (!queue.isEmpty()) {
            int[] top = queue.poll();
            int u = top[1];
            if (finalized[u]) continue;
            finalized[u] = true;

            for (int[] edge : graph.get(u)) {
                int v = edge[0];
                int weight = edge[1];
                if (!finalized[v] && dist[u] + weight < dist[v]) {
                    dist[v] = dist[u] + weight;
                    parent[v] = u;
                    queue.add(new int[]{dist[v], v});
                }
            }
        }
        return dist;
    }

    static List<Integer> pathTo(int target, int[] parent, int source) {
        List<Integer> path = new ArrayList<>();
        for (int v = target; v != -1; v = parent[v]) {
            path.add(v);
        }
        Collections.reverse(path);
        return path.get(0) == source ? path : List.of();
    }

    // all-pairs shortest paths by brute force, used as an independent answer to compare Dijkstra against
    static int[][] floydWarshall(int n, int[][] weights) {
        int[][] d = new int[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (i == j) d[i][j] = 0;
                else d[i][j] = weights[i][j] == NO_EDGE ? Integer.MAX_VALUE : weights[i][j];
            }
        }
        for (int k = 0; k < n; k++) {
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    if (d[i][k] != Integer.MAX_VALUE && d[k][j] != Integer.MAX_VALUE && d[i][k] + d[k][j] < d[i][j]) {
                        d[i][j] = d[i][k] + d[k][j];
                    }
                }
            }
        }
        return d;
    }

    static final int NO_EDGE = Integer.MIN_VALUE; // a sentinel that cannot clash with a real (even negative) weight

    static List<List<int[]>> graphFromMatrix(int n, int[][] weights) {
        List<List<int[]>> graph = emptyGraph(n);
        for (int u = 0; u < n; u++) {
            for (int v = 0; v < n; v++) {
                if (u != v && weights[u][v] != NO_EDGE) graph.get(u).add(new int[]{v, weights[u][v]});
            }
        }
        return graph;
    }
}
