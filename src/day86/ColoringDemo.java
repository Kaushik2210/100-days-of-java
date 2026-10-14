import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;

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

        System.out.println();
        // "crown" graph: a0..a3 and b0..b3, with a_i joined to b_j for every i != j (bipartite, chromatic number 2)
        List<List<Integer>> crown = emptyGraph(8);
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                if (i != j) addEdge(crown, i, 4 + j);
            }
        }
        int[] badOrder = {0, 4, 1, 5, 2, 6, 3, 7};   // a0, b0, a1, b1, ...
        int[] goodOrder = {0, 1, 2, 3, 4, 5, 6, 7};  // all of one side first, then the other
        int[] badColors = greedyColor(crown, badOrder);
        int[] goodColors = greedyColor(crown, goodOrder);
        System.out.println("crown graph, which really needs only 2 colors (bipartite: " + (twoColor(crown) != null) + "):");
        System.out.println("  greedy in the order a0,b0,a1,b1,... uses " + colorsUsed(badColors) + " colors " + Arrays.toString(badColors));
        System.out.println("  greedy in the order a0..a3,b0..b3 uses " + colorsUsed(goodColors) + " colors " + Arrays.toString(goodColors));

        int exact = chromaticNumber(crown);
        System.out.println("  backtracking finds the true chromatic number = " + exact);

        System.out.println();
        Random random = new Random(86); // fixed seed so the run is repeatable
        int trials = 400;
        int bipartiteMismatches = 0;
        int improperTwoColorings = 0;
        int chromaticMismatches = 0;
        int greedyImproper = 0;
        int greedyOverBound = 0;
        int greedyBelowOptimal = 0;
        int greedyWasSuboptimal = 0;
        int bipartiteCount = 0;
        for (int t = 0; t < trials; t++) {
            int n = 1 + random.nextInt(6);
            List<List<Integer>> g = emptyGraph(n);
            int density = random.nextInt(70);
            for (int u = 0; u < n; u++) {
                for (int v = u + 1; v < n; v++) {
                    if (random.nextInt(100) < density) addEdge(g, u, v); // no self-loops: they admit no coloring
                }
            }

            int[] two = twoColor(g);
            boolean oracleBipartite = existsProperColoring(g, 2);
            if ((two != null) != oracleBipartite) bipartiteMismatches++;
            if (two != null) {
                bipartiteCount++;
                if (!isProperColoring(g, two)) improperTwoColorings++;
            }

            int chromatic = chromaticNumber(g);
            int bruteChromatic = 1;
            while (!existsProperColoring(g, bruteChromatic)) bruteChromatic++;
            if (chromatic != bruteChromatic) chromaticMismatches++;

            List<Integer> shuffled = new ArrayList<>();
            for (int i = 0; i < n; i++) shuffled.add(i);
            Collections.shuffle(shuffled, random);
            int[] order = shuffled.stream().mapToInt(Integer::intValue).toArray();
            int[] greedy = greedyColor(g, order);

            int maxDegree = 0;
            for (List<Integer> neighbors : g) maxDegree = Math.max(maxDegree, neighbors.size());
            int used = colorsUsed(greedy);
            if (!isProperColoring(g, greedy)) greedyImproper++;
            if (used > maxDegree + 1) greedyOverBound++;
            if (used < chromatic) greedyBelowOptimal++;
            if (used > chromatic) greedyWasSuboptimal++;
        }
        System.out.println("over " + trials + " random graphs (up to 6 vertices, " + bipartiteCount + " of them bipartite):");
        System.out.println("  BFS bipartite verdict vs brute force over all 2-colorings, mismatches = " + bipartiteMismatches);
        System.out.println("  2-colorings returned that were not proper = " + improperTwoColorings);
        System.out.println("  backtracking chromatic number vs brute force, mismatches = " + chromaticMismatches);
        System.out.println("  greedy colorings that were not proper = " + greedyImproper);
        System.out.println("  greedy results exceeding the (max degree + 1) bound = " + greedyOverBound);
        System.out.println("  greedy results using fewer colors than the chromatic number (impossible) = " + greedyBelowOptimal);
        System.out.println("  greedy used more colors than necessary in " + greedyWasSuboptimal + " of the " + trials + " runs");
    }

    static int[] greedyColor(List<List<Integer>> graph, int[] order) {
        int n = graph.size();
        int[] color = new int[n];
        Arrays.fill(color, -1);

        for (int u : order) {
            boolean[] used = new boolean[n + 1];
            for (int v : graph.get(u)) {
                if (color[v] != -1) used[color[v]] = true;
            }
            int c = 0;
            while (used[c]) c++;
            color[u] = c;
        }
        return color;
    }

    static int colorsUsed(int[] color) {
        int max = -1;
        for (int c : color) max = Math.max(max, c);
        return max + 1;
    }

    static boolean colorable(List<List<Integer>> graph, int[] color, int vertex, int k) {
        if (vertex == graph.size()) return true;
        for (int c = 0; c < k; c++) {
            boolean safe = true;
            for (int v : graph.get(vertex)) {
                if (color[v] == c) { safe = false; break; }
            }
            if (!safe) continue;
            color[vertex] = c;
            if (colorable(graph, color, vertex + 1, k)) return true;
            color[vertex] = -1;
        }
        return false;
    }

    static int chromaticNumber(List<List<Integer>> graph) {
        for (int k = 1; ; k++) {
            int[] color = new int[graph.size()];
            Arrays.fill(color, -1);
            if (colorable(graph, color, 0, k)) return k;
        }
    }

    // independent oracle: literally try every one of the k^n assignments and test each for properness
    static boolean existsProperColoring(List<List<Integer>> graph, int k) {
        int n = graph.size();
        long total = 1;
        for (int i = 0; i < n; i++) total *= k;
        for (long code = 0; code < total; code++) {
            int[] color = new int[n];
            long rest = code;
            for (int i = 0; i < n; i++) {
                color[i] = (int) (rest % k);
                rest /= k;
            }
            if (isProperColoring(graph, color)) return true;
        }
        return false;
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
