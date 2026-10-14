import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class ColoringDemo {

    public static void main(String[] args) {
        report("4-cycle (even)", cycle(4));
        report("5-cycle (odd)", cycle(5));
        report("path of 5 vertices (a tree)", path(5));

        // two separate pieces: a bipartite path {0,1,2} and a triangle {3,4,5}
        List<List<Integer>> mixed = emptyGraph(6);
        addEdge(mixed, 0, 1);
        addEdge(mixed, 1, 2);
        addEdge(mixed, 3, 4);
        addEdge(mixed, 4, 5);
        addEdge(mixed, 5, 3);
        report("path + triangle (disconnected)", mixed);

        List<List<Integer>> loop = emptyGraph(2);
        addEdge(loop, 0, 1);
        addEdge(loop, 1, 1);
        report("edge plus a self-loop on vertex 1", loop);
    }

    static void report(String name, List<List<Integer>> graph) {
        int[] color = twoColor(graph);
        if (color == null) {
            System.out.println(name + ": NOT bipartite");
        } else {
            System.out.println(name + ": bipartite, coloring = " + Arrays.toString(color)
                + " (proper: " + isProperColoring(graph, color) + ")");
        }
    }

    static List<List<Integer>> emptyGraph(int n) {
        List<List<Integer>> graph = new ArrayList<>();
        for (int i = 0; i < n; i++) graph.add(new ArrayList<>());
        return graph;
    }

    static void addEdge(List<List<Integer>> graph, int a, int b) {
        graph.get(a).add(b);
        if (a != b) graph.get(b).add(a); // an undirected self-loop only needs listing once
    }

    static List<List<Integer>> cycle(int n) {
        List<List<Integer>> graph = emptyGraph(n);
        for (int i = 0; i < n; i++) addEdge(graph, i, (i + 1) % n);
        return graph;
    }

    static List<List<Integer>> path(int n) {
        List<List<Integer>> graph = emptyGraph(n);
        for (int i = 0; i + 1 < n; i++) addEdge(graph, i, i + 1);
        return graph;
    }

    static int[] twoColor(List<List<Integer>> graph) {
        int n = graph.size();
        int[] color = new int[n];
        Arrays.fill(color, -1);

        for (int start = 0; start < n; start++) {
            if (color[start] != -1) continue;
            color[start] = 0;
            Queue<Integer> queue = new LinkedList<>();
            queue.add(start);

            while (!queue.isEmpty()) {
                int u = queue.poll();
                for (int v : graph.get(u)) {
                    if (color[v] == -1) {
                        color[v] = 1 - color[u];
                        queue.add(v);
                    } else if (color[v] == color[u]) {
                        return null;
                    }
                }
            }
        }
        return color;
    }

    static boolean isProperColoring(List<List<Integer>> graph, int[] color) {
        for (int u = 0; u < graph.size(); u++) {
            for (int v : graph.get(u)) {
                if (color[u] == color[v]) return false;
            }
        }
        return true;
    }
}
