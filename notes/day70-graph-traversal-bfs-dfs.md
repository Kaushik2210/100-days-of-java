# Day 70: Graph Traversal — BFS & DFS

Day 63's level-order and depth-first tree traversals generalize directly to graphs — but a graph adds a real complication a tree never has: **cycles**. Walking a tree's `left`/`right` pointers can never lead back to a node already visited, but a graph's edges can, so every graph traversal needs an explicit **visited set** to avoid looping forever.

## BFS: breadth-first, level by level

Breadth-first search visits a starting vertex, then all of *its* neighbors, then all of *their* unvisited neighbors, and so on outward in expanding rings — exactly Day 63's level-order traversal, using the same queue-based mechanism (Day 61), just walking a graph's adjacency list (Day 69) instead of a binary tree's two fixed children.

```java
List<Integer> bfs(Map<Integer, List<Integer>> graph, int start) {
    List<Integer> order = new ArrayList<>();
    Set<Integer> visited = new HashSet<>();     // Day 24's HashSet -- O(1) "have I seen this already?"
    Queue<Integer> queue = new LinkedList<>();

    visited.add(start);
    queue.add(start);

    while (!queue.isEmpty()) {
        int current = queue.poll();
        order.add(current);
        for (int neighbor : graph.getOrDefault(current, List.of())) {
            if (!visited.contains(neighbor)) {
                visited.add(neighbor);   // mark visited when ENQUEUED, not when dequeued
                queue.add(neighbor);
            }
        }
    }
    return order;
}
```

Marking a vertex `visited` at the moment it's *enqueued* (not when it's later dequeued and processed) is the detail that actually prevents cycles from causing infinite loops — without it, the same vertex could be added to the queue multiple times by different neighbors before it's ever processed once.

BFS's defining property: it explores in expanding rings from the start, which means the *first* time it reaches any vertex is guaranteed to be via the shortest path from the start, measured in number of edges — this is exactly why BFS is the standard tool for shortest-path-by-edge-count on an unweighted graph.
