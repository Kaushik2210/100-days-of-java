# Day 83: Minimum Spanning Tree — Kruskal's Algorithm

Days 81 and 82 asked "what is the cheapest path from one vertex to the others?" A different weighted-graph question is: "what is the cheapest way to *connect everything*?" Think of laying cable between towns so that every town is reachable from every other, using as little total cable as possible. That is the **minimum spanning tree (MST)** problem.

## What a spanning tree is

For a connected, undirected, weighted graph with `V` vertices, a **spanning tree** is a subset of the edges that:

- connects **all** `V` vertices, and
- contains **no cycle**.

Those two conditions together force it to have exactly `V - 1` edges: fewer would leave something disconnected, and an extra edge would close a cycle. The **minimum** spanning tree is the spanning tree whose edge weights add up to the smallest total. A graph can have several MSTs when edge weights tie, but they all share the same total weight.

## Kruskal's idea: take the cheapest edge that does not make a cycle

Kruskal's algorithm is greedy in the Day 74 sense, and here the greedy choice is provably safe:

1. Sort all edges by weight, ascending.
2. Go through them in that order. Keep an edge if it connects two vertices that are **not already connected**; skip it if they are (it would close a cycle).
3. Stop when `V - 1` edges have been kept.

"Are these two vertices already connected?" is exactly the question Day 80's Union-Find answers in near-constant time, and it was introduced with this very algorithm in mind. `union` returning `false` means "already connected, skip this edge."

```java
// each edge is {u, v, weight}
int kruskal(int n, int[][] edges, List<int[]> chosen) {
    int[][] sorted = edges.clone();
    Arrays.sort(sorted, (a, b) -> Integer.compare(a[2], b[2])); // Day 26's Comparator: cheapest first

    UnionFind uf = new UnionFind(n); // Day 80's optimized structure
    int total = 0;
    for (int[] edge : sorted) {
        if (uf.union(edge[0], edge[1])) { // true only if the endpoints were in different groups
            chosen.add(edge);
            total += edge[2];
            if (chosen.size() == n - 1) break; // a spanning tree is complete
        }
    }
    return total;
}
```

## Why the greedy choice is safe

The justification is the **cut property**: split the vertices into any two non-empty groups. Among the edges crossing between the two groups, the cheapest one belongs to some minimum spanning tree. Kruskal's rule applies this over and over. When it considers the cheapest remaining edge that joins two different components, that edge is the cheapest one crossing the cut between "this component" and "everything else," so keeping it can never rule out an optimal answer.

This is the same kind of argument as Day 74's activity selection (an exchange argument), and it is exactly what coin change lacked, which is why greedy was wrong there and is right here.
