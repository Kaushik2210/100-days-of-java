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
