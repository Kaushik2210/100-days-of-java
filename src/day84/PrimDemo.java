import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

public class PrimDemo {

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

    static int prim(List<List<int[]>> graph, int start, List<int[]> chosen) {
        int n = graph.size();
        boolean[] inTree = new boolean[n];
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
}
