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
