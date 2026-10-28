import java.util.*;

// Capstone: the graph tools from Days 69 to 86 solving one problem on the same random networks,
// each checked against a different method. If independent algorithms agree, the code is probably right.
public class CapstoneDemo {

    record Edge(int u, int v, int w) {}

    static final int INF = Integer.MAX_VALUE / 4;

    public static void main(String[] args) {
        Random random = new Random(100);
        int trials = 2000;
        int distMismatch = 0, mstMismatch = 0, componentMismatch = 0, topoBad = 0, connectedTrials = 0;

        for (int t = 0; t < trials; t++) {
            int n = 2 + random.nextInt(10);
            List<Edge> edges = new ArrayList<>();
            int m = random.nextInt(n * 2 + 1);
            for (int i = 0; i < m; i++) {
                int u = random.nextInt(n), v = random.nextInt(n);
                if (u != v) edges.add(new Edge(u, v, 1 + random.nextInt(20)));   // undirected, positive weights
            }

            // Shortest paths: Dijkstra (Day 81) vs Bellman-Ford (Day 82) vs Floyd-Warshall (a third, independent oracle)
            int[] dij = dijkstra(n, edges, 0);
            int[] bf = bellmanFord(n, edges, 0);
            int[][] fw = floyd(n, edges);
            if (!Arrays.equals(dij, bf) || !Arrays.equals(dij, fw[0])) distMismatch++;

            // Components: union-find (Day 80) vs plain DFS (Day 70)
            int uf = componentsUnionFind(n, edges);
            int dfs = componentsDfs(n, edges);
            if (uf != dfs) componentMismatch++;

            // Minimum spanning tree: Kruskal (Day 83) vs Prim (Day 84), only meaningful when connected
            if (uf == 1) {
                connectedTrials++;
                if (kruskal(n, edges) != prim(n, edges)) mstMismatch++;
            }

            // Topological sort (Day 85) on a random DAG hidden behind a shuffled labelling
            if (!topoValid(n, random)) topoBad++;
        }

        System.out.println("== Capstone: independent algorithms agreeing on " + trials + " random graphs ==");
        System.out.println("Dijkstra vs Bellman-Ford vs Floyd-Warshall distances: " + distMismatch + " mismatches");
        System.out.println("union-find components vs DFS components:             " + componentMismatch + " mismatches");
        System.out.println("Kruskal vs Prim total weight (" + connectedTrials + " connected graphs):   " + mstMismatch + " mismatches");
        System.out.println("topological orders that violated an edge:            " + topoBad);

        System.out.println();
        System.out.println("== Hand-checked example ==");
        List<Edge> g = List.of(new Edge(0, 1, 4), new Edge(0, 2, 1), new Edge(2, 1, 2), new Edge(1, 3, 1), new Edge(2, 3, 5));
        System.out.println("distances from 0: " + Arrays.toString(dijkstra(4, g, 0)) + " (expected [0, 3, 1, 4])");
        System.out.println("MST weight: " + kruskal(4, g) + " and " + prim(4, g) + " (expected 4: edges 0-2, 2-1, 1-3)");
    }

    static int[] dijkstra(int n, List<Edge> edges, int src) {
        List<List<int[]>> adj = adjacency(n, edges);
        int[] dist = new int[n];
        Arrays.fill(dist, INF);
        dist[src] = 0;
        PriorityQueue<int[]> pq = new PriorityQueue<>((a, b) -> Integer.compare(a[1], b[1]));
        pq.add(new int[]{src, 0});
        while (!pq.isEmpty()) {
            int[] cur = pq.poll();
            if (cur[1] > dist[cur[0]]) continue;               // stale entry
            for (int[] e : adj.get(cur[0])) {
                int nd = cur[1] + e[1];
                if (nd < dist[e[0]]) { dist[e[0]] = nd; pq.add(new int[]{e[0], nd}); }
            }
        }
        return dist;
    }

    static int[] bellmanFord(int n, List<Edge> edges, int src) {
        int[] dist = new int[n];
        Arrays.fill(dist, INF);
        dist[src] = 0;
        for (int round = 0; round < n - 1; round++) {
            for (Edge e : edges) {
                if (dist[e.u()] + e.w() < dist[e.v()]) dist[e.v()] = dist[e.u()] + e.w();
                if (dist[e.v()] + e.w() < dist[e.u()]) dist[e.u()] = dist[e.v()] + e.w();
            }
        }
        return dist;
    }

    static int[][] floyd(int n, List<Edge> edges) {
        int[][] d = new int[n][n];
        for (int[] row : d) Arrays.fill(row, INF);
        for (int i = 0; i < n; i++) d[i][i] = 0;
        for (Edge e : edges) {
            d[e.u()][e.v()] = Math.min(d[e.u()][e.v()], e.w());
            d[e.v()][e.u()] = Math.min(d[e.v()][e.u()], e.w());
        }
        for (int k = 0; k < n; k++)
            for (int i = 0; i < n; i++)
                for (int j = 0; j < n; j++)
                    if (d[i][k] + d[k][j] < d[i][j]) d[i][j] = d[i][k] + d[k][j];
        return d;
    }

    static List<List<int[]>> adjacency(int n, List<Edge> edges) {
        List<List<int[]>> adj = new ArrayList<>();
        for (int i = 0; i < n; i++) adj.add(new ArrayList<>());
        for (Edge e : edges) {
            adj.get(e.u()).add(new int[]{e.v(), e.w()});
            adj.get(e.v()).add(new int[]{e.u(), e.w()});
        }
        return adj;
    }

    static int find(int[] parent, int x) {
        while (parent[x] != x) { parent[x] = parent[parent[x]]; x = parent[x]; }   // path halving
        return x;
    }

    static int componentsUnionFind(int n, List<Edge> edges) {
        int[] parent = new int[n];
        for (int i = 0; i < n; i++) parent[i] = i;
        int count = n;
        for (Edge e : edges) {
            int a = find(parent, e.u()), b = find(parent, e.v());
            if (a != b) { parent[a] = b; count--; }
        }
        return count;
    }

    static int componentsDfs(int n, List<Edge> edges) {
        List<List<int[]>> adj = adjacency(n, edges);
        boolean[] seen = new boolean[n];
        int count = 0;
        for (int s = 0; s < n; s++) {
            if (seen[s]) continue;
            count++;
            Deque<Integer> stack = new ArrayDeque<>();
            stack.push(s);
            seen[s] = true;
            while (!stack.isEmpty()) {
                int u = stack.pop();
                for (int[] e : adj.get(u)) if (!seen[e[0]]) { seen[e[0]] = true; stack.push(e[0]); }
            }
        }
        return count;
    }

    static int kruskal(int n, List<Edge> edges) {
        List<Edge> sorted = new ArrayList<>(edges);
        sorted.sort(Comparator.comparingInt(Edge::w));
        int[] parent = new int[n];
        for (int i = 0; i < n; i++) parent[i] = i;
        int total = 0;
        for (Edge e : sorted) {
            int a = find(parent, e.u()), b = find(parent, e.v());
            if (a != b) { parent[a] = b; total += e.w(); }
        }
        return total;
    }

    static int prim(int n, List<Edge> edges) {
        List<List<int[]>> adj = adjacency(n, edges);
        boolean[] in = new boolean[n];
        PriorityQueue<int[]> pq = new PriorityQueue<>((a, b) -> Integer.compare(a[1], b[1]));
        pq.add(new int[]{0, 0});
        int total = 0;
        while (!pq.isEmpty()) {
            int[] cur = pq.poll();
            if (in[cur[0]]) continue;
            in[cur[0]] = true;
            total += cur[1];
            for (int[] e : adj.get(cur[0])) if (!in[e[0]]) pq.add(new int[]{e[0], e[1]});
        }
        return total;
    }

    // Build a random DAG hidden behind a shuffled labelling, run Kahn's algorithm, check every edge goes forward.
    static boolean topoValid(int n, Random random) {
        List<Integer> labels = new ArrayList<>();
        for (int i = 0; i < n; i++) labels.add(i);
        Collections.shuffle(labels, random);
        List<int[]> dag = new ArrayList<>();
        for (int i = 0; i < n; i++)
            for (int j = i + 1; j < n; j++)
                if (random.nextInt(4) == 0) dag.add(new int[]{labels.get(i), labels.get(j)});

        int[] indeg = new int[n];
        List<List<Integer>> out = new ArrayList<>();
        for (int i = 0; i < n; i++) out.add(new ArrayList<>());
        for (int[] e : dag) { out.get(e[0]).add(e[1]); indeg[e[1]]++; }
        Deque<Integer> queue = new ArrayDeque<>();
        for (int i = 0; i < n; i++) if (indeg[i] == 0) queue.add(i);
        int[] position = new int[n];
        int placed = 0;
        while (!queue.isEmpty()) {
            int u = queue.poll();
            position[u] = placed++;
            for (int v : out.get(u)) if (--indeg[v] == 0) queue.add(v);
        }
        if (placed != n) return false;                        // a DAG must always be fully ordered
        for (int[] e : dag) if (position[e[0]] >= position[e[1]]) return false;
        return true;
    }
}
