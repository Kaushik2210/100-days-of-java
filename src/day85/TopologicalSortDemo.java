import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;

public class TopologicalSortDemo {

    static final int WHITE = 0;
    static final int GRAY = 1;
    static final int BLACK = 2;

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

        List<Integer> dfsOrder = topologicalOrderDfs(graph);
        System.out.println("DFS-based order is also valid: " + isValidOrder(graph, dfsOrder));

        System.out.println();
        // add a prerequisite loop: Capstone -> Intro
        addEdge(graph, 5, 0);
        List<Integer> withCycle = kahn(graph);
        System.out.println("after adding Capstone -> Intro: Kahn ordered " + withCycle.size() + " of " + courses.length
            + " courses -> cycle detected: " + (withCycle.size() < courses.length));
        System.out.println("DFS three-color check also reports a cycle: " + topologicalOrderDfs(graph).isEmpty());

        System.out.println();
        // a -> b, a -> c, b -> c is a valid DAG, but its undirected shape is a triangle
        List<List<Integer>> triangle = emptyGraph(3);
        addEdge(triangle, 0, 1);
        addEdge(triangle, 0, 2);
        addEdge(triangle, 1, 2);
        System.out.println("a->b, a->c, b->c (a DAG):");
        System.out.println("  Kahn orders all 3 vertices (no cycle): " + (kahn(triangle).size() == 3));
        System.out.println("  Union-Find (Day 80, ignores direction) claims a cycle: " + unionFindSeesCycle(triangle)
            + "  <- wrong for directed graphs");

        System.out.println();
        Random random = new Random(85); // fixed seed so the run is repeatable
        int trials = 1000;
        int dagCount = 0;
        int cyclicCount = 0;
        int verdictMismatches = 0;
        int invalidOrders = 0;
        for (int t = 0; t < trials; t++) {
            int n = 1 + random.nextInt(8);
            List<List<Integer>> g = emptyGraph(n);
            boolean forceDag = random.nextBoolean();

            if (forceDag) {
                // only edges that go forward in a hidden random ordering, so no cycle can exist
                List<Integer> hidden = new ArrayList<>();
                for (int i = 0; i < n; i++) hidden.add(i);
                Collections.shuffle(hidden, random);
                for (int i = 0; i < n; i++) {
                    for (int j = i + 1; j < n; j++) {
                        if (random.nextInt(100) < 35) addEdge(g, hidden.get(i), hidden.get(j));
                    }
                }
            } else {
                int edgeCount = random.nextInt(2 * n + 1);
                for (int e = 0; e < edgeCount; e++) {
                    addEdge(g, random.nextInt(n), random.nextInt(n)); // may create self-loops and cycles
                }
            }

            boolean hasCycle = hasCycleByClosure(g); // independent oracle: some vertex can reach itself
            List<Integer> kahnOrder = kahn(g);
            List<Integer> dfsResult = topologicalOrderDfs(g);

            boolean kahnSaysCycle = kahnOrder.size() < n;
            boolean dfsSaysCycle = dfsResult.isEmpty(); // n >= 1 here, so an empty result can only mean a cycle
            if (kahnSaysCycle != hasCycle || dfsSaysCycle != hasCycle) verdictMismatches++;

            if (hasCycle) {
                cyclicCount++;
            } else {
                dagCount++;
                if (!isValidOrder(g, kahnOrder) || !isValidOrder(g, dfsResult)) invalidOrders++;
            }
        }
        System.out.println("Kahn and DFS vs a transitive-closure oracle over " + trials + " random directed graphs ("
            + dagCount + " DAGs, " + cyclicCount + " with cycles):");
        System.out.println("  cycle verdict mismatches (either algorithm) = " + verdictMismatches);
        System.out.println("  DAG results that were not valid topological orders = " + invalidOrders);
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

    static boolean dfsVisit(List<List<Integer>> graph, int u, int[] color, List<Integer> finished) {
        color[u] = GRAY;
        for (int v : graph.get(u)) {
            if (color[v] == GRAY) return false;
            if (color[v] == WHITE && !dfsVisit(graph, v, color, finished)) return false;
        }
        color[u] = BLACK;
        finished.add(u);
        return true;
    }

    static List<Integer> topologicalOrderDfs(List<List<Integer>> graph) {
        int n = graph.size();
        int[] color = new int[n];
        List<Integer> finished = new ArrayList<>();
        for (int v = 0; v < n; v++) {
            if (color[v] == WHITE && !dfsVisit(graph, v, color, finished)) return new ArrayList<>();
        }
        Collections.reverse(finished);
        return finished;
    }

    // Day 80's cycle test applied to a directed graph with the directions thrown away
    static boolean unionFindSeesCycle(List<List<Integer>> graph) {
        int n = graph.size();
        int[] parent = new int[n];
        for (int i = 0; i < n; i++) parent[i] = i;
        for (int u = 0; u < n; u++) {
            for (int v : graph.get(u)) {
                int a = find(parent, u);
                int b = find(parent, v);
                if (a == b) return true;
                parent[a] = b;
            }
        }
        return false;
    }

    static int find(int[] parent, int x) {
        while (parent[x] != x) x = parent[x];
        return x;
    }

    // oracle: build the reachability matrix with Floyd-Warshall; a cycle exists iff some vertex reaches itself
    static boolean hasCycleByClosure(List<List<Integer>> graph) {
        int n = graph.size();
        boolean[][] reach = new boolean[n][n];
        for (int u = 0; u < n; u++) {
            for (int v : graph.get(u)) reach[u][v] = true;
        }
        for (int k = 0; k < n; k++) {
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    if (reach[i][k] && reach[k][j]) reach[i][j] = true;
                }
            }
        }
        for (int i = 0; i < n; i++) {
            if (reach[i][i]) return true;
        }
        return false;
    }

    // a valid topological order is a permutation of all vertices where every edge goes from earlier to later
    static boolean isValidOrder(List<List<Integer>> graph, List<Integer> order) {
        int n = graph.size();
        if (order.size() != n) return false;
        int[] position = new int[n];
        Arrays.fill(position, -1);
        for (int i = 0; i < n; i++) {
            int v = order.get(i);
            if (position[v] != -1) return false;
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
