# Day 80: Union-Find (Disjoint Set) Data Structure

Day 70's BFS and DFS answer "what is reachable from here?" for a graph that stays fixed. Many problems instead ask a *dynamic* question: items are being merged into groups one connection at a time, and between merges you need to know "are these two items already in the same group?" Re-running a traversal after every new edge would be wasteful. **Union-Find** (also called a **disjoint-set union**, or DSU) answers both questions in nearly constant time.

## The two operations

- **`find(x)`** — return a representative (the "root") of the group containing `x`. Two items are in the same group exactly when their `find` results are equal.
- **`union(a, b)`** — merge the group containing `a` with the group containing `b`.

Initially every item is in a group by itself.

## Representation: a forest stored in one array

Each group is a tree, but nothing needs pointers or node objects. A single `int[] parent` array is enough: `parent[i]` is the item that `i` points up to, and a root is an item that points to itself.

```java
class UnionFind {
    private int[] parent;

    UnionFind(int n) {
        parent = new int[n];
        for (int i = 0; i < n; i++) parent[i] = i; // every item starts as the root of its own group
    }

    int find(int x) {
        while (parent[x] != x) {   // climb until reaching a root
            x = parent[x];
        }
        return x;
    }

    void union(int a, int b) {
        parent[find(a)] = find(b); // hang one group's root underneath the other's
    }

    boolean connected(int a, int b) {
        return find(a) == find(b);
    }
}
```

This is correct, and every operation is as cheap as the height of the tree it touches. The trouble is that nothing controls that height.

## How the naive version degrades

Union the items in a chain, `union(0, 1)`, then `union(1, 2)`, then `union(2, 3)`, and so on. Each call hangs the previous group's root under the new item, producing one long path `0 → 1 → 2 → 3 → ... → n-1`. A single `find(0)` now walks all `n - 1` links, so `n` operations can cost O(n²) in total. It is Day 64's degenerate BST all over again: a tree with no mechanism keeping it shallow turns into a linked list.

## Optimization 1: union by size

Instead of hanging one root under the other arbitrarily, always hang the **smaller** tree under the **larger** one. A tree only gets taller when it is placed under a root that is at least as big, so each time an item's depth increases, the size of its group at least doubles. An item's depth can therefore grow at most `log₂ n` times, which caps every tree's height at O(log n).

## Optimization 2: path compression

Every `find(x)` already visits each item on the path from `x` up to the root. Path compression takes advantage of that visit: after finding the root, it re-points every item on the path **directly at the root**, flattening the tree so later finds on those items take one step.

```java
class UnionFind {
    private int[] parent;
    private int[] size;
    private int components;

    UnionFind(int n) {
        parent = new int[n];
        size = new int[n];
        components = n;
        for (int i = 0; i < n; i++) {
            parent[i] = i;
            size[i] = 1;
        }
    }

    int find(int x) {
        if (parent[x] != x) {
            parent[x] = find(parent[x]); // path compression: point x straight at the root
        }
        return parent[x];
    }

    boolean union(int a, int b) {
        int rootA = find(a);
        int rootB = find(b);
        if (rootA == rootB) return false; // already in the same group -- nothing to merge

        if (size[rootA] < size[rootB]) { // make rootA the larger one
            int temp = rootA;
            rootA = rootB;
            rootB = temp;
        }
        parent[rootB] = rootA;           // smaller tree hangs under the larger one
        size[rootA] += size[rootB];
        components--;
        return true;
    }

    boolean connected(int a, int b) {
        return find(a) == find(b);
    }

    int componentCount() {
        return components;
    }
}
```

The recursion in `find` is safe here: union by size keeps every tree's height at O(log n), so the recursion can never get deep enough to threaten a `StackOverflowError` (Day 42), which the unoptimized version's long chains easily could.

`union` returning a `boolean` is deliberate. A `false` result means the two items were already connected, which is exactly the signal needed for the applications below.

## Complexity

With **both** optimizations, a sequence of `m` operations on `n` items costs O(m × α(n)) in total, where α is the *inverse Ackermann function*. It grows so slowly that it is at most 4 for any `n` that could ever fit in a computer's memory, so each operation is nearly constant time in practice. Using only one of the two optimizations still gives O(log n) amortized per operation, a large improvement over the naive O(n).

## Where it gets used

- **Counting connected components**: start with `n` components and subtract one on every successful `union`.
- **Cycle detection in an undirected graph**: process the edges one at a time. If an edge's two endpoints are *already* connected (`union` returns `false`), that edge would close a cycle.
- **Kruskal's minimum spanning tree algorithm** (Day 83), which relies on exactly that cycle check to decide which edges to keep.
- **Dynamic connectivity** in general: network connectivity, grouping equivalent items, or the "friend circles" style of problem, wherever groups only ever merge.

Union-Find handles merging but not splitting. If connections can also be *removed*, it is the wrong tool.
