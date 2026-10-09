import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.PriorityQueue;

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
        int n = graph.size();
        int[] dist = new int[n];
        Arrays.fill(dist, Integer.MAX_VALUE);
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
                    queue.add(new int[]{dist[v], v});
                }
            }
        }
        return dist;
    }
}
