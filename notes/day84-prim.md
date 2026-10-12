# Day 84: Minimum Spanning Tree — Prim's Algorithm

Day 83's Kruskal builds an MST by sorting *all* edges and merging many small components together. **Prim's algorithm** reaches the same answer by growing a **single** tree outward from one starting vertex, one edge at a time. Both are greedy, and both rest on the same cut property, but they feel quite different to implement.

## The idea

Keep a set of vertices already in the tree. Repeatedly:

1. Look at every edge that connects a vertex **inside** the tree to one **outside** it.
2. Take the cheapest such edge.
3. Add the outside vertex it reaches to the tree.

Stop once every vertex is in the tree. At each step the tree-versus-everything-else split is a cut, and the cheapest edge crossing it is exactly what the cut property (Day 83) says is safe to keep.

## Implementation with a priority queue

Step 2 is "give me the minimum," so a priority queue (Day 62) is the natural fit. This is structurally almost identical to Day 81's Dijkstra, including the same trick for stale queue entries: Java's `PriorityQueue` cannot lower an existing entry's key, so the code adds fresh entries and skips outdated ones when they surface.

```java
// graph: adjacency list where each entry is {neighbor, weight}
int prim(List<List<int[]>> graph, int start, List<int[]> chosen) {
    int n = graph.size();
    boolean[] inTree = new boolean[n];
    PriorityQueue<int[]> queue = new PriorityQueue<>((a, b) -> Integer.compare(a[0], b[0]));
    // queue entries: {weight of the edge reaching this vertex, vertex, vertex it came from}
    queue.add(new int[]{0, start, -1});

    int total = 0;
    while (!queue.isEmpty()) {
        int[] top = queue.poll();
        int weight = top[0];
        int u = top[1];
        if (inTree[u]) continue;            // stale entry: this vertex joined the tree via a cheaper edge already

        inTree[u] = true;
        total += weight;
        if (top[2] != -1) chosen.add(new int[]{top[2], u, weight});

        for (int[] edge : graph.get(u)) {
            if (!inTree[edge[0]]) {
                queue.add(new int[]{edge[1], edge[0], u}); // offer the edge from u to its not-yet-included neighbor
            }
        }
    }
    return total;
}
```

The start vertex enters with weight `0` and no parent, so it contributes nothing to the total. After that, every vertex is pulled in through the cheapest edge that touches the growing tree at that moment.

## Complexity

Each edge is offered to the queue at most twice (once from each endpoint), so the queue sees O(E) entries, and each insert or removal costs O(log E) = O(log V). Total: **O((V + E) log V)**, the same as Dijkstra and the same asymptotic cost as Kruskal's O(E log V).

## A dense-graph variant: no heap at all

On a **dense** graph, where `E` is close to `V²`, the heap is overhead. A simple array-based version keeps `best[v]`, the cheapest edge weight currently connecting `v` to the tree, and finds the next vertex by scanning all `V` entries for the minimum:

```java
int primDense(int[][] weights, int n) { // weights[u][v] = edge weight, or NO_EDGE if absent
    int[] best = new int[n];
    boolean[] inTree = new boolean[n];
    Arrays.fill(best, Integer.MAX_VALUE);
    best[0] = 0;

    int total = 0;
    for (int step = 0; step < n; step++) {
        int u = -1;
        for (int v = 0; v < n; v++) {                  // linear scan replaces the priority queue
            if (!inTree[v] && (u == -1 || best[v] < best[u])) u = v;
        }
        if (best[u] == Integer.MAX_VALUE) break;       // everything left is unreachable
        inTree[u] = true;
        total += best[u];

        for (int v = 0; v < n; v++) {
            if (!inTree[v] && weights[u][v] != NO_EDGE && weights[u][v] < best[v]) {
                best[v] = weights[u][v];               // cheaper edge into the tree found for v
            }
        }
    }
    return total;
}
```

This is **O(V²)** regardless of `E`. When `E` is near `V²` that beats `(V + E) log V`, and it uses a plain adjacency matrix (Day 69), which is the better representation for dense graphs anyway.

## Disconnected graphs

Prim only ever grows the tree of the component containing its start vertex. If the graph has several components, a single run covers just one of them. To get a spanning *forest* like Day 83's, restart Prim from every vertex that is still unvisited. Kruskal needs no such care, because sorting all edges up front handles every component in one pass.

## Prim vs Dijkstra: a one-line difference that changes everything

The two algorithms look almost identical, which makes them easy to confuse. The difference is what the priority queue is keyed on:

- **Dijkstra** keys on the **total distance from the source**: relaxing `u → v` offers `dist[u] + w`.
- **Prim** keys on the **single edge weight**: relaxing `u → v` offers just `w`.

That is why Dijkstra gives shortest *paths* and Prim gives a cheapest *connection*. They can disagree: for a triangle with edges `0–1` (2), `1–2` (2), and `0–2` (3), the MST uses `0–1` and `1–2` for a total of 4, while Dijkstra's shortest-path tree from vertex 0 uses `0–1` and `0–2` (reaching vertex 2 for 3 rather than 4) for a total of 5. A cheapest network and a set of cheapest routes from one point are different things.

## Choosing between Kruskal and Prim

- **Kruskal** — natural when the input is an edge list, handles disconnected graphs without extra work, and does well on sparse graphs.
- **Prim** — natural with an adjacency list or matrix, and the dense O(V²) form wins when edges outnumber what a heap handles well.
- Both give an MST of identical total weight.
