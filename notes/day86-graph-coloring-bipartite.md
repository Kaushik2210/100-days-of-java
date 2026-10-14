# Day 86: Graph Coloring & Bipartite Graph Checking

Day 85 asked about ordering vertices along directed edges. Today's question is about *labeling* vertices so that vertices joined by an edge never get the same label. This is **graph coloring**, and the simplest nontrivial case, using just two colors, is the **bipartite** check.

## Proper coloring

A **proper coloring** assigns each vertex a color so that no two adjacent vertices share one. The smallest number of colors that makes a proper coloring possible is the graph's **chromatic number**.

Real problems that are secretly graph coloring: scheduling exams so that no student sits two at once (vertices are exams, an edge means "shares a student", colors are time slots), assigning radio frequencies to nearby transmitters, and allocating CPU registers to variables that are alive at the same time.

## Bipartite means two-colorable

A graph is **bipartite** when its vertices can be split into two groups with every edge going *between* the groups, never inside one. Equivalently, it can be properly colored with just two colors. Trees and even-length cycles are bipartite, while any graph containing a triangle is not.

## Checking with BFS

Two-coloring is forced once you pick a color for a starting vertex: all of its neighbors must be the *other* color, all of their neighbors must be the first color again, and so on. That is exactly a BFS (Day 70) that alternates colors by level. The graph fails to be bipartite the moment an edge connects two vertices that already have the *same* color.

```java
// returns the 0/1 coloring, or null if the graph is not bipartite
int[] twoColor(List<List<Integer>> graph) {
    int n = graph.size();
    int[] color = new int[n];
    Arrays.fill(color, -1);                  // -1 means "not colored yet"

    for (int start = 0; start < n; start++) { // restart for every component of a disconnected graph
        if (color[start] != -1) continue;
        color[start] = 0;
        Queue<Integer> queue = new LinkedList<>();
        queue.add(start);

        while (!queue.isEmpty()) {
            int u = queue.poll();
            for (int v : graph.get(u)) {
                if (color[v] == -1) {
                    color[v] = 1 - color[u];  // forced: the opposite of its neighbor
                    queue.add(v);
                } else if (color[v] == color[u]) {
                    return null;              // an edge inside one color group: not bipartite
                }
            }
        }
    }
    return color;
}
```

Two details are easy to miss. The outer loop is required because a disconnected graph needs each component colored on its own, and one component being bipartite says nothing about the next. And a self-loop `u — u` is caught naturally: `v` equals `u`, so it has the same color as itself.

The whole check is **O(V + E)**, the same cost as the BFS it is built on.

## Why odd cycles are exactly the obstruction

A graph is bipartite **if and only if it contains no cycle of odd length**.

- *Odd cycle implies not bipartite:* going around a cycle forces the colors to alternate, so after an odd number of steps you come back to the start needing the opposite color from the one it already has. Contradiction, so two colors are never enough.
- *No odd cycle implies bipartite:* in each component, color every vertex by the parity of its BFS distance from the start. An edge joining two vertices of the same parity would close an odd cycle, and by assumption there is none.

That is why the BFS above only has to look for one thing, an edge between same-colored vertices: finding one is precisely finding an odd cycle.

## Beyond two colors

Asking whether a graph can be colored with **three or more** colors is a very different matter. Deciding whether `k` colors suffice is **NP-complete** for any `k ≥ 3`: no known algorithm is fast in the worst case, in contrast to the O(V + E) check for `k = 2`. Two practical approaches follow.

**Greedy coloring** processes vertices in some order and gives each the smallest color not already used by its colored neighbors.

```java
int[] greedyColor(List<List<Integer>> graph, int[] order) {
    int n = graph.size();
    int[] color = new int[n];
    Arrays.fill(color, -1);

    for (int u : order) {
        boolean[] used = new boolean[n + 1];
        for (int v : graph.get(u)) {
            if (color[v] != -1) used[color[v]] = true; // colors taken by already-colored neighbors
        }
        int c = 0;
        while (used[c]) c++;                           // smallest color nobody adjacent is using
        color[u] = c;
    }
    return color;
}
```

It is fast, O(V + E), and always uses at most `Δ + 1` colors, where `Δ` is the largest vertex degree, since a vertex has at most `Δ` neighbors blocking colors. But the number of colors it uses **depends on the order** it visits vertices in, and a bad order can use far more colors than necessary. It is a heuristic, not an optimal algorithm.

**Backtracking** (Day 75) finds the true minimum by trying colors vertex by vertex and undoing a choice whenever it would put two adjacent vertices in the same color. It is exact but exponential in the worst case, which is why it is only practical for small graphs.

```java
boolean colorable(List<List<Integer>> graph, int[] color, int vertex, int k) {
    if (vertex == graph.size()) return true;           // every vertex colored without conflict
    for (int c = 0; c < k; c++) {
        boolean safe = true;
        for (int v : graph.get(vertex)) {
            if (color[v] == c) { safe = false; break; } // prune: a neighbor already has this color
        }
        if (!safe) continue;
        color[vertex] = c;                               // choose
        if (colorable(graph, color, vertex + 1, k)) return true; // explore
        color[vertex] = -1;                              // un-choose
    }
    return false;
}
```

Calling it with `k = 1, 2, 3, ...` until it first succeeds gives the chromatic number. (Both coloring routines assume the graph has no self-loops: a vertex adjacent to itself can never get a color different from its own, so no proper coloring exists at all.)

## Where this shows up

- **Bipartite checking** underlies matching problems (assigning workers to jobs, students to projects) and any "split into two conflicting-free teams" question.
- **Scheduling and allocation** problems with conflicts: exam timetables, frequency assignment, register allocation in compilers.
- **Sudoku** is graph coloring in disguise: each cell is a vertex, cells that share a row, column, or box are adjacent, and the nine digits are the nine colors. A backtracking solver is exactly the coloring routine above.
