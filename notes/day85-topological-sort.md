# Day 85: Topological Sorting & Cycle Detection

Days 81 through 84 worked on *undirected* weighted graphs. Today's problem is about **directed** graphs and about *ordering*: when edges mean "must come before," in what order can everything be done? Course prerequisites, build-system dependencies, and "install A before B" package managers all have this shape.

## Directed acyclic graphs and topological order

A **directed acyclic graph (DAG)** is a directed graph with no cycles. A **topological order** of a DAG is a way of listing every vertex so that for every edge `u → v`, `u` appears **before** `v`.

- If a graph has a cycle, no topological order exists. A cycle like `a → b → c → a` would need `a` before `b` before `c` before `a`, which is impossible.
- If a graph is a DAG, at least one topological order exists, and usually many. `a → c` and `b → c` is satisfied by both `a, b, c` and `b, a, c`.

So computing a topological order and detecting a cycle are two sides of the same question.

## Kahn's algorithm: repeatedly peel off vertices with nothing in front of them

A vertex's **in-degree** is the number of edges pointing *into* it. A vertex with in-degree `0` has no unmet prerequisites, so it can safely go first. Kahn's algorithm uses that observation directly:

1. Compute every vertex's in-degree.
2. Put all in-degree-`0` vertices into a queue (Day 61).
3. Repeatedly remove a vertex from the queue and append it to the output. "Delete" its outgoing edges by decrementing each neighbor's in-degree; any neighbor that reaches `0` joins the queue.

```java
// graph: adjacency list where graph.get(u) holds the vertices u points to
List<Integer> kahn(List<List<Integer>> graph) {
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
            if (inDegree[v] == 0) queue.add(v);   // all of v's prerequisites are now placed
        }
    }
    return order; // shorter than n exactly when the graph has a cycle
}
```

## The cycle detection comes for free

Vertices on a cycle each have an incoming edge from another vertex on that cycle, so their in-degrees can never drop to `0` and they never enter the queue. When the loop ends, if the output holds fewer than `n` vertices, the leftover vertices are exactly the ones that are on a cycle or depend on one. The check is simply `order.size() < n`.

This runs in **O(V + E)**: every vertex is enqueued at most once, and every edge is touched once when its source vertex is processed.
