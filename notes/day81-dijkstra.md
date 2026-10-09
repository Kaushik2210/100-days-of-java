# Day 81: Shortest Path — Dijkstra's Algorithm

Day 70's BFS finds the shortest path in an *unweighted* graph, where "shortest" means fewest edges. Real graphs usually have **weights**: road lengths, travel times, network latencies. A route with fewer edges is not necessarily cheaper, so BFS stops being correct. **Dijkstra's algorithm** finds the cheapest path from one source vertex to every other vertex in a graph whose edge weights are all **non-negative**.

## The idea: a greedy algorithm that finalizes one vertex at a time

Dijkstra is the first of Day 74's greedy strategies that is applied to a graph, and unlike Day 74's coin change, the greedy choice here is provably safe. It keeps a `dist[]` array of the best known distance to each vertex (initially infinity, except the source at `0`) and repeats:

1. Take the **unfinalized vertex with the smallest known distance**.
2. **Finalize** it: no cheaper path to it can exist, because every other route would have to pass through a vertex that is at least as far away, and weights cannot be negative.
3. **Relax** each of its outgoing edges: if going through this vertex gives a neighbor a cheaper path than its current `dist`, update it.

The operation in step 3 is called **relaxation**. For an edge `u → v` with weight `w`: if `dist[u] + w < dist[v]`, set `dist[v] = dist[u] + w`.

## Picking the closest vertex fast: a priority queue

Step 1 is exactly "give me the minimum," which is what Day 62's priority queue (built on Day 66's heap) provides in O(log n). The queue holds `(distance, vertex)` pairs ordered by distance.

```java
int[] dijkstra(List<List<int[]>> graph, int source) {
    int n = graph.size();
    int[] dist = new int[n];
    Arrays.fill(dist, Integer.MAX_VALUE);     // MAX_VALUE stands for "unreachable so far"
    boolean[] finalized = new boolean[n];
    dist[source] = 0;

    PriorityQueue<int[]> queue = new PriorityQueue<>((a, b) -> Integer.compare(a[0], b[0])); // Day 26's Comparator
    queue.add(new int[]{0, source});

    while (!queue.isEmpty()) {
        int[] top = queue.poll();
        int u = top[1];
        if (finalized[u]) continue;            // a stale entry: u was already finalized via a cheaper path
        finalized[u] = true;

        for (int[] edge : graph.get(u)) {      // each edge is {neighbor, weight}
            int v = edge[0];
            int weight = edge[1];
            if (!finalized[v] && dist[u] + weight < dist[v]) {
                dist[v] = dist[u] + weight;    // relax: found a cheaper way to reach v
                queue.add(new int[]{dist[v], v});
            }
        }
    }
    return dist;
}
```

The graph is an adjacency list (Day 69), where each entry stores the neighbor and the edge weight. Java's `PriorityQueue` has no "decrease the key of an existing entry" operation, so when a vertex's distance improves the code simply adds a *new* entry. The older, now-worse entry stays in the queue and is skipped later by the `finalized[u]` check when it eventually reaches the front.

Vertices that `dist` still marks as `Integer.MAX_VALUE` at the end are unreachable from the source.

## Recovering the actual path

`dist[]` says how far each vertex is, not how to get there. Recording a `parent[]` entry whenever a relaxation succeeds (`parent[v] = u`) keeps the route. To read a path, start at the target and follow `parent` links back to the source, then reverse the result, the same collect-backwards-then-reverse pattern as Day 78's LCS traceback.

```java
List<Integer> pathTo(int target, int[] parent, int source) {
    List<Integer> path = new ArrayList<>();
    for (int v = target; v != -1; v = parent[v]) { // parent[source] is -1, which ends the walk
        path.add(v);
    }
    Collections.reverse(path);
    return path.get(0) == source ? path : List.of(); // empty list if the target was never reached
}
```

## Why edge weights must be non-negative

Step 2 of the algorithm, "no cheaper path to this vertex can exist," is only true because adding more edges to a path can never make it cheaper. With a negative edge, that stops being true.

Take three vertices with edges `0 → 1` (weight 2), `0 → 2` (weight 3), and `2 → 1` (weight **-2**). Dijkstra finalizes vertex 1 at distance 2 before it ever examines vertex 2. But the route `0 → 2 → 1` costs `3 + (-2) = 1`, which is cheaper. By the time that route is discovered, vertex 1 is already finalized and the algorithm never revisits it, so it reports the wrong answer.

It is tempting to fix this by letting finalized vertices be re-processed whenever their distance improves. For graphs with negative edges but no negative cycles that variant does converge on correct distances, but it is no longer Dijkstra: it can do far more work than the `O((V + E) log V)` bound, and on a graph with a negative *cycle* it never terminates, since every lap around the cycle makes the distances smaller again. The principled tool for negative weights is the Bellman-Ford algorithm on Day 82.

## Complexity

Each successful relaxation adds one queue entry, so there are at most `E` entries. Every entry costs O(log E) to insert and to remove, and `log E` is O(log V) because `E ≤ V²`. Total: **O((V + E) log V)**. That is close to linear for sparse graphs, which is why Dijkstra scales to real road networks.

## When to use what

- **BFS (Day 70)** — all edges effectively cost the same (an unweighted graph). Simpler and O(V + E).
- **Dijkstra** — weighted edges, none negative. The standard choice for routing.
- **Bellman-Ford (Day 82)** — negative edge weights are possible, or negative cycles need detecting.
