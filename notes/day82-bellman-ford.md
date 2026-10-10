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

## Negative cycles: when no answer exists

A **negative cycle** is a loop of edges whose weights add up to less than zero. Going around it lowers the total cost every lap, so for any vertex you can reach *through* such a cycle there is no cheapest path: you can always do one more lap and get cheaper. "Shortest distance" is simply undefined there.

Bellman-Ford can tell you this is happening. If there is no negative cycle, `V - 1` sweeps are enough and the distances are final, so **no edge can still be relaxed** afterward. If an extra, `V`-th sweep *can* still improve some edge, that is only possible because a negative cycle is feeding it.

```java
class BellmanFordResult {
    int[] dist;
    boolean negativeCycleReachable;
}

BellmanFordResult bellmanFordWithCycleCheck(int n, int[][] edges, int source) {
    BellmanFordResult result = new BellmanFordResult();
    int[] dist = new int[n];
    Arrays.fill(dist, Integer.MAX_VALUE);
    dist[source] = 0;

    for (int pass = 1; pass <= n - 1; pass++) {
        boolean changed = false;
        for (int[] edge : edges) {
            if (dist[edge[0]] != Integer.MAX_VALUE && dist[edge[0]] + edge[2] < dist[edge[1]]) {
                dist[edge[1]] = dist[edge[0]] + edge[2];
                changed = true;
            }
        }
        if (!changed) break;
    }

    for (int[] edge : edges) { // the extra sweep: anything that still improves proves a negative cycle
        if (dist[edge[0]] != Integer.MAX_VALUE && dist[edge[0]] + edge[2] < dist[edge[1]]) {
            result.negativeCycleReachable = true;
        }
    }
    result.dist = dist;
    return result;
}
```

Note the word *reachable*. The `dist[u] != Integer.MAX_VALUE` guard means a negative cycle that the source cannot get to is never even looked at. That is the correct behavior: it has no effect on distances from this source, so it is not a reason to reject the answer. When a reachable negative cycle is reported, the distances of the vertices it can reach should be treated as meaningless.

## Complexity and when to choose it

Each of up to `V - 1` sweeps touches all `E` edges, so Bellman-Ford is **O(V × E)** time with O(V) extra space. That is noticeably slower than Dijkstra's O((V + E) log V). The trade is generality:

- **Dijkstra (Day 81)** — faster, but needs every weight to be non-negative.
- **Bellman-Ford** — slower, but handles negative weights and tells you about negative cycles.

Real uses of that extra power include currency arbitrage detection (take the negative logarithm of each exchange rate and a profitable trading loop becomes a negative cycle) and distance-vector routing protocols such as RIP, whose routers repeatedly exchange and relax distance estimates in essentially a distributed Bellman-Ford.
