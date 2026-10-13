import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class TopologicalSortDemo {

    public static void main(String[] args) {
        // course prerequisites: an edge a -> b means "take a before b"
        String[] courses = {"Intro", "DataStructures", "Algorithms", "Databases", "Compilers", "Capstone"};
        List<List<Integer>> graph = emptyGraph(courses.length);
        addEdge(graph, 0, 1); // Intro -> DataStructures
        addEdge(graph, 1, 2); // DataStructures -> Algorithms
        addEdge(graph, 1, 3); // DataStructures -> Databases
        addEdge(graph, 2, 4); // Algorithms -> Compilers
        addEdge(graph, 3, 5); // Databases -> Capstone
        addEdge(graph, 4, 5); // Compilers -> Capstone

        List<Integer> order = kahn(graph);
        System.out.println("course order (Kahn's algorithm):");
        for (int v : order) {
            System.out.println("  " + courses[v]);
        }
        System.out.println("covers all " + courses.length + " courses: " + (order.size() == courses.length)
            + ", valid ordering: " + isValidOrder(graph, order));

        System.out.println();
        // add a prerequisite loop: Capstone -> Intro
        addEdge(graph, 5, 0);
        List<Integer> withCycle = kahn(graph);
        System.out.println("after adding Capstone -> Intro: ordered " + withCycle.size() + " of " + courses.length
            + " courses -> cycle detected: " + (withCycle.size() < courses.length));
    }

    static List<List<Integer>> emptyGraph(int n) {
        List<List<Integer>> graph = new ArrayList<>();
        for (int i = 0; i < n; i++) graph.add(new ArrayList<>());
        return graph;
    }

    static void addEdge(List<List<Integer>> graph, int from, int to) {
        graph.get(from).add(to);
    }

    static List<Integer> kahn(List<List<Integer>> graph) {
        int n = graph.size();
        int[] inDegree = new int[n];
        for (List<Integer> neighbors : graph) {
            for (int v : neighbors) inDegree[v]++;
        }

        Queue<Integer> queue = new LinkedList<>();
        for (int v = 0; v < n; v++) {
            if (inDegree[v] == 0) queue.add(v);
        }

        List<Integer> order = new ArrayList<>();
        while (!queue.isEmpty()) {
            int u = queue.poll();
            order.add(u);
            for (int v : graph.get(u)) {
                inDegree[v]--;
                if (inDegree[v] == 0) queue.add(v);
            }
        }
        return order;
    }

    // a valid topological order is a permutation of all vertices where every edge goes from earlier to later
    static boolean isValidOrder(List<List<Integer>> graph, List<Integer> order) {
        int n = graph.size();
        if (order.size() != n) return false;
        int[] position = new int[n];
        java.util.Arrays.fill(position, -1);
        for (int i = 0; i < n; i++) {
            int v = order.get(i);
            if (position[v] != -1) return false; // vertex listed twice
            position[v] = i;
        }
        for (int u = 0; u < n; u++) {
            for (int v : graph.get(u)) {
                if (position[u] >= position[v]) return false;
            }
        }
        return true;
    }
}
