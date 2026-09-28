import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

public class GraphTraversalDemo {

    public static void main(String[] args) {
        //     1
        //    / \
        //   2   3
        //   |   |
        //   4---5
        Map<Integer, List<Integer>> graph = new HashMap<>();
        addEdge(graph, 1, 2);
        addEdge(graph, 1, 3);
        addEdge(graph, 2, 4);
        addEdge(graph, 3, 5);
        addEdge(graph, 4, 5); // creates a cycle: 1-2-4-5-3-1

        System.out.println("bfs from 1:            " + bfs(graph, 1));

        List<Integer> recursiveOrder = new ArrayList<>();
        dfsRecursive(graph, 1, new HashSet<>(), recursiveOrder);
        System.out.println("dfs (recursive) from 1: " + recursiveOrder);

        System.out.println("dfs (iterative) from 1: " + dfsIterative(graph, 1));

        System.out.println();
        System.out.println("All three visit the same vertex SET (order may differ):");
        System.out.println("bfs set             = " + new HashSet<>(bfs(graph, 1)));
        System.out.println("dfs recursive set   = " + new HashSet<>(recursiveOrder));
        System.out.println("dfs iterative set   = " + new HashSet<>(dfsIterative(graph, 1)));
    }

    static void addEdge(Map<Integer, List<Integer>> graph, int a, int b) {
        graph.computeIfAbsent(a, k -> new ArrayList<>()).add(b);
        graph.computeIfAbsent(b, k -> new ArrayList<>()).add(a);
    }

    static List<Integer> bfs(Map<Integer, List<Integer>> graph, int start) {
        List<Integer> order = new ArrayList<>();
        Set<Integer> visited = new HashSet<>();
        Queue<Integer> queue = new LinkedList<>();

        visited.add(start);
        queue.add(start);

        while (!queue.isEmpty()) {
            int current = queue.poll();
            order.add(current);
            for (int neighbor : graph.getOrDefault(current, List.of())) {
                if (!visited.contains(neighbor)) {
                    visited.add(neighbor);
                    queue.add(neighbor);
                }
            }
        }
        return order;
    }

    static void dfsRecursive(Map<Integer, List<Integer>> graph, int current, Set<Integer> visited, List<Integer> order) {
        visited.add(current);
        order.add(current);
        for (int neighbor : graph.getOrDefault(current, List.of())) {
            if (!visited.contains(neighbor)) {
                dfsRecursive(graph, neighbor, visited, order);
            }
        }
    }

    static List<Integer> dfsIterative(Map<Integer, List<Integer>> graph, int start) {
        List<Integer> order = new ArrayList<>();
        Set<Integer> visited = new HashSet<>();
        Deque<Integer> stack = new ArrayDeque<>();

        stack.push(start);
        while (!stack.isEmpty()) {
            int current = stack.pop();
            if (visited.contains(current)) continue;
            visited.add(current);
            order.add(current);
            for (int neighbor : graph.getOrDefault(current, List.of())) {
                if (!visited.contains(neighbor)) {
                    stack.push(neighbor);
                }
            }
        }
        return order;
    }
}
