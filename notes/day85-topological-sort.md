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

## The DFS approach: reverse finishing order

Day 70's DFS gives a second way to topologically sort. Run a depth-first search from every unvisited vertex, and record each vertex at the moment it **finishes**, meaning after all of its descendants have been explored. Reading the finished list backward gives a topological order.

Why that works: a vertex only finishes after everything reachable from it has finished first. So for any edge `u → v`, `v` finishes before `u` does, which puts `u` ahead of `v` once the list is reversed.

## Cycle detection in a directed graph: three colors

To detect cycles during the DFS, each vertex needs one of three states rather than a plain visited flag:

- **WHITE** — not yet discovered.
- **GRAY** — currently on the recursion stack, still being explored.
- **BLACK** — fully finished.

An edge that leads to a **GRAY** vertex goes back to something still in progress, which means a cycle. An edge to a BLACK vertex is harmless: that vertex was fully explored and cannot lead back here.

```java
// returns false if a cycle is found; otherwise fills `finished` in finishing order
boolean dfsVisit(List<List<Integer>> graph, int u, int[] color, List<Integer> finished) {
    color[u] = GRAY;
    for (int v : graph.get(u)) {
        if (color[v] == GRAY) return false;                       // back edge: u points to an ancestor on the stack
        if (color[v] == WHITE && !dfsVisit(graph, v, color, finished)) return false;
    }
    color[u] = BLACK;
    finished.add(u);                                              // postorder: recorded after all descendants
    return true;
}

List<Integer> topologicalOrderDfs(List<List<Integer>> graph) { // returns an empty list if there is a cycle
    int n = graph.size();
    int[] color = new int[n];
    List<Integer> finished = new ArrayList<>();
    for (int v = 0; v < n; v++) {
        if (color[v] == WHITE && !dfsVisit(graph, v, color, finished)) return new ArrayList<>();
    }
    Collections.reverse(finished);
    return finished;
}
```

A self-loop `u → u` is caught by the same rule: while exploring `u` (GRAY), the edge back to `u` finds a GRAY vertex.

## Why Day 80's Union-Find does not work here

Day 80 used Union-Find to detect cycles, and it is tempting to reuse it. It only works for **undirected** graphs. Union-Find only knows that two vertices are connected, not which direction the edges point.

Consider `a → b`, `a → c`, `b → c`. This is a perfectly good DAG (order: `a, b, c`). But treated as undirected, the three edges form a triangle, and Union-Find would report a cycle. Directed cycles need an algorithm that respects direction: Kahn's in-degree counting or the three-color DFS.

## Choosing between the two

- **Kahn's** — iterative (no recursion depth to worry about, Day 42), and naturally processes things "layer by layer," which suits scheduling problems where tasks that become ready at the same time can run in parallel.
- **DFS** — a few lines when you already have DFS machinery, and the finishing times are useful in other algorithms. The recursion depth can reach `V` on a long dependency chain.

Both are O(V + E). Neither produces a unique answer in general; any order satisfying every edge is valid. A topological order is unique only when the DAG contains a path through all vertices.
