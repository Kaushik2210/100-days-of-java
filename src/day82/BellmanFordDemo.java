import java.util.Arrays;

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
