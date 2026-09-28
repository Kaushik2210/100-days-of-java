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

## DFS: depth-first, all the way down one path first

Depth-first search does the opposite: follow one path as deep as it goes before backtracking to try another — Day 63's preorder traversal, generalized the same way. Recursion (Day 52) is the natural fit, since the call stack itself tracks the path to backtrack along.

```java
void dfsRecursive(Map<Integer, List<Integer>> graph, int current, Set<Integer> visited, List<Integer> order) {
    visited.add(current);
    order.add(current);
    for (int neighbor : graph.getOrDefault(current, List.of())) {
        if (!visited.contains(neighbor)) {
            dfsRecursive(graph, neighbor, visited, order); // go as deep as possible before trying a sibling
        }
    }
}
```

The same traversal can also be written iteratively with an explicit stack (Day 60) instead of relying on the call stack — useful when recursion depth risks a `StackOverflowError` (Day 42) on a very large or deeply chained graph:

```java
List<Integer> dfsIterative(Map<Integer, List<Integer>> graph, int start) {
    List<Integer> order = new ArrayList<>();
    Set<Integer> visited = new HashSet<>();
    Deque<Integer> stack = new ArrayDeque<>(); // Day 62's deque, used as a stack

    stack.push(start);
    while (!stack.isEmpty()) {
        int current = stack.pop();
        if (visited.contains(current)) continue; // may have been pushed more than once -- skip if already done
        visited.add(current);
        order.add(current);
        for (int neighbor : graph.getOrDefault(current, List.of())) {
            if (!visited.contains(neighbor)) {
                stack.push(neighbor);
            }
        }
    }
    return order;
}
```

Unlike BFS, the iterative version marks a vertex visited at *dequeue* (pop) time rather than push time, and instead just tolerates duplicate pushes with a `continue` check — either strategy is valid, but they aren't interchangeable between BFS and DFS without changing the resulting order subtly.

## Choosing between BFS and DFS

- **BFS** — shortest path by edge count on an unweighted graph; finding all vertices within some limited number of "hops"; anything where "closest first" genuinely matters.
- **DFS** — detecting cycles; topological sorting (Day 85); exploring every path fully (maze-solving, puzzle-solving); generally simpler to write recursively, and uses less memory than BFS on a wide, shallow graph (a queue full of one entire "ring" of vertices can be much larger than a stack holding just the current path).

Both are O(V + E): every vertex is visited once, and every edge is examined once (from whichever end reaches it first) — the same total work, just a different order of exploring it.
