# Day 82: Shortest Path — Bellman-Ford Algorithm

Day 81 ended on a failure: Dijkstra finalizes a vertex the moment it is removed from the priority queue, and that is only safe when no edge weight is negative. **Bellman-Ford** drops that assumption. It handles negative edge weights correctly, and it can also *detect* the situation where no shortest path exists at all because of a negative cycle.

## The idea: relax every edge, repeatedly

Dijkstra is clever about *which* vertex to process next. Bellman-Ford does something much blunter: it relaxes **every edge in the graph** (Day 81's relaxation step: if `dist[u] + w < dist[v]`, update `dist[v]`), and then does that whole sweep again, up to `V - 1` times.

Why `V - 1` sweeps are enough: a shortest path that does not repeat a vertex has at most `V - 1` edges. After sweep number `k`, every vertex whose shortest path uses at most `k` edges already holds its correct distance, because that path's edges were relaxed in order across the sweeps. So after `V - 1` sweeps every shortest simple path has been fully relaxed.

Since each sweep simply goes through all the edges, the natural representation is a plain list of edges rather than Day 69's adjacency list.

```java
// each edge is {from, to, weight}; weights may be negative
int[] bellmanFord(int n, int[][] edges, int source) {
    int[] dist = new int[n];
    Arrays.fill(dist, Integer.MAX_VALUE);
    dist[source] = 0;

    for (int pass = 1; pass <= n - 1; pass++) {
        boolean changed = false;
        for (int[] edge : edges) {
            int u = edge[0];
            int v = edge[1];
            int w = edge[2];
            if (dist[u] != Integer.MAX_VALUE && dist[u] + w < dist[v]) { // skip edges out of unreachable vertices
                dist[v] = dist[u] + w;
                changed = true;
            }
        }
        if (!changed) break; // a full sweep with no improvement means nothing can improve any more
    }
    return dist;
}
```

Two details matter here. The `dist[u] != Integer.MAX_VALUE` guard stops the algorithm from "relaxing" an edge out of a vertex that has not been reached yet, which would both be meaningless and overflow when a weight is added to `MAX_VALUE`. And the `changed` flag lets the loop stop early: if a whole sweep changes nothing, further sweeps cannot either, so on many graphs it finishes in far fewer than `V - 1` passes.

## Where Dijkstra's counterexample goes

Take Day 81's graph: `0 → 1` (2), `0 → 2` (3), `2 → 1` (-2). Sweep 1 sets `dist[1] = 2` and `dist[2] = 3` (depending on edge order it may also catch `2 → 1` in the same sweep). Whichever order the edges come in, a second sweep relaxes `2 → 1` to give `3 + (-2) = 1`. Bellman-Ford never "finalizes" anything early, so a cheaper route discovered later is simply picked up on the next sweep.
