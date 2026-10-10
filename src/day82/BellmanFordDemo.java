import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class BellmanFordDemo {

    public static void main(String[] args) {
        // Day 81's counterexample: Dijkstra answered 2 for vertex 1; the true shortest route is 0 -> 2 -> 1 = 1
        int[][] small = {{0, 1, 2}, {0, 2, 3}, {2, 1, -2}};
        System.out.println("Day 81 counterexample, distances from 0: " + show(bellmanFord(3, small, 0)) + " (expected [0, 1, 3])");

        // CLRS textbook graph with negative edges: s=0, t=1, x=2, y=3, z=4, plus an isolated vertex 5
        int[][] textbook = {
            {0, 1, 6}, {0, 3, 7},
            {1, 2, 5}, {1, 3, 8}, {1, 4, -4},
            {2, 1, -2},
            {3, 2, -3}, {3, 4, 9},
            {4, 0, 2}, {4, 2, 7}
        };
        System.out.println("textbook graph, distances from 0: " + show(bellmanFord(6, textbook, 0))
            + " (expected [0, 2, 4, 7, -2, unreachable])");

        System.out.println();
        // 1 -> 2 (-3) and 2 -> 1 (+1) form a loop worth -2 per lap, and vertex 0 can reach it
        int[][] reachableCycle = {{0, 1, 1}, {1, 2, -3}, {2, 1, 1}};
        System.out.println("negative cycle reachable from 0: "
            + bellmanFordWithCycleCheck(3, reachableCycle, 0).negativeCycleReachable + " (expected true)");

        // the negative loop 2 <-> 3 exists, but the source (vertex 0) has no path into it
        int[][] unreachableCycle = {{0, 1, 4}, {2, 3, -1}, {3, 2, -1}};
        BellmanFordResult unreachable = bellmanFordWithCycleCheck(4, unreachableCycle, 0);
        System.out.println("negative cycle exists but is not reachable from 0: reported = " + unreachable.negativeCycleReachable
            + " (expected false), distances = " + show(unreachable.dist));

        System.out.println();
        Random random = new Random(82); // fixed seed so the run is repeatable
        int trials = 600;
        int cycleMismatches = 0;
        int distanceMismatches = 0;
        int sourcesWithReachableCycle = 0;
        int cleanSourcesWithNegativeEdge = 0;
        int sourcesChecked = 0;
        for (int t = 0; t < trials; t++) {
            int n = 1 + random.nextInt(8);
            List<int[]> edgeList = new ArrayList<>();
            boolean hasNegativeEdge = false;
            for (int u = 0; u < n; u++) {
                for (int v = 0; v < n; v++) {
                    if (u != v && random.nextInt(100) < 30) {
                        int w = random.nextInt(20) - 4; // weights from -4 to 15
                        if (w < 0) hasNegativeEdge = true;
                        edgeList.add(new int[]{u, v, w});
                    }
                }
            }
            int[][] edges = edgeList.toArray(new int[0][]);
            long[][] oracle = floydWarshall(n, edges);

            for (int source = 0; source < n; source++) {
                sourcesChecked++;
                boolean expectedCycle = false;
                for (int i = 0; i < n; i++) {
                    if (oracle[source][i] < INF && oracle[i][i] < 0) expectedCycle = true;
                }

                BellmanFordResult result = bellmanFordWithCycleCheck(n, edges, source);
                if (result.negativeCycleReachable != expectedCycle) cycleMismatches++;

                if (expectedCycle) {
                    sourcesWithReachableCycle++;
                } else {
                    if (hasNegativeEdge) cleanSourcesWithNegativeEdge++;
                    for (int target = 0; target < n; target++) {
                        long expected = oracle[source][target];
                        long actual = result.dist[target] == Integer.MAX_VALUE ? INF : result.dist[target];
                        if (expected != actual) distanceMismatches++;
                    }
                }
            }
        }
        System.out.println("Bellman-Ford vs Floyd-Warshall over " + trials + " random graphs with negative edges ("
            + sourcesChecked + " source runs):");
        System.out.println("  negative-cycle verdict mismatches = " + cycleMismatches
            + " (" + sourcesWithReachableCycle + " runs genuinely had a reachable negative cycle)");
        System.out.println("  distance mismatches on cycle-free runs = " + distanceMismatches
            + " (" + cleanSourcesWithNegativeEdge + " of those runs involved at least one negative edge)");
    }

    static final long INF = Long.MAX_VALUE / 4;

    static class BellmanFordResult {
        int[] dist;
        boolean negativeCycleReachable;
    }

    static BellmanFordResult bellmanFordWithCycleCheck(int n, int[][] edges, int source) {
        BellmanFordResult result = new BellmanFordResult();
        int[] dist = new int[n];
        Arrays.fill(dist, Integer.MAX_VALUE);
        dist[source] = 0;

        for (int pass = 1; pass <= n - 1; pass++) {
            boolean changed = false;
            for (int[] edge : edges) {
                if (dist[edge[0]] != Integer.MAX_VALUE && dist[edge[0]] + edge[2] < dist[edge[1]]) {
                    dist[edge[1]] = dist[edge[0]] + edge[2];
                    changed = true;
                }
            }
            if (!changed) break;
        }

        for (int[] edge : edges) {
            if (dist[edge[0]] != Integer.MAX_VALUE && dist[edge[0]] + edge[2] < dist[edge[1]]) {
                result.negativeCycleReachable = true;
            }
        }
        result.dist = dist;
        return result;
    }

    // all-pairs shortest paths by brute force, used as an independent answer; long values leave room for negative cycles
    static long[][] floydWarshall(int n, int[][] edges) {
        long[][] d = new long[n][n];
        for (long[] row : d) Arrays.fill(row, INF);
        for (int i = 0; i < n; i++) d[i][i] = 0;
        for (int[] edge : edges) d[edge[0]][edge[1]] = Math.min(d[edge[0]][edge[1]], edge[2]);

        for (int k = 0; k < n; k++) {
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    if (d[i][k] < INF && d[k][j] < INF && d[i][k] + d[k][j] < d[i][j]) {
                        d[i][j] = d[i][k] + d[k][j];
                    }
                }
            }
        }
        return d;
    }

    static int[] bellmanFord(int n, int[][] edges, int source) {
        int[] dist = new int[n];
        Arrays.fill(dist, Integer.MAX_VALUE);
        dist[source] = 0;

        for (int pass = 1; pass <= n - 1; pass++) {
            boolean changed = false;
            for (int[] edge : edges) {
                int u = edge[0];
                int v = edge[1];
                int w = edge[2];
                if (dist[u] != Integer.MAX_VALUE && dist[u] + w < dist[v]) {
                    dist[v] = dist[u] + w;
                    changed = true;
                }
            }
            if (!changed) break;
        }
        return dist;
    }

    static String show(int[] dist) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < dist.length; i++) {
            if (i > 0) sb.append(", ");
            sb.append(dist[i] == Integer.MAX_VALUE ? "unreachable" : String.valueOf(dist[i]));
        }
        return sb.append("]").toString();
    }
}
