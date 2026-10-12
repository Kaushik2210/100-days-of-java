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
