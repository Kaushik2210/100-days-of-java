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
