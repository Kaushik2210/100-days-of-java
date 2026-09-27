# Day 69: Graphs — Representation (Adjacency List & Adjacency Matrix)

Day 63's tree is a special case of something more general: a **graph**, made of **vertices** (nodes) connected by **edges**, with no restriction on how many connections a vertex can have or whether cycles can form. A tree is just a graph with no cycles and exactly one path between any two vertices; a graph drops both constraints, which is why it can model things a tree can't — a road network, a social network, a dependency graph where cycles are possible.

## Vocabulary

- **Vertex (node)** — one point in the graph.
- **Edge** — a connection between two vertices.
- **Directed vs undirected** — a directed edge only goes one way (Twitter's "follows"); an undirected edge goes both ways (Facebook's mutual "friends").
- **Weighted vs unweighted** — a weighted edge carries a cost or distance (a road's length); an unweighted edge just means "connected." Day 81's Dijkstra's algorithm needs weights; Day 70's BFS/DFS don't.
- **Degree** — the number of edges touching a vertex (for directed graphs, split into in-degree and out-degree).

## Adjacency list: a map from vertex to its neighbors

The most common representation stores, for each vertex, a list of the vertices it connects to — essentially Day 25's `HashMap` mapping each vertex to a `List` (Day 23) of neighbors.

```java
class Graph {
    private Map<Integer, List<Integer>> adjacencyList = new HashMap<>();

    void addVertex(int vertex) {
        adjacencyList.putIfAbsent(vertex, new ArrayList<>()); // Day 25's putIfAbsent
    }

    void addEdge(int from, int to) {
        addVertex(from);
        addVertex(to);
        adjacencyList.get(from).add(to);
        adjacencyList.get(to).add(from); // omit this line for a directed graph
    }

    List<Integer> neighborsOf(int vertex) {
        return adjacencyList.getOrDefault(vertex, new ArrayList<>());
    }
}
```

Adding the reverse edge (`to` back to `from`) is what makes this an *undirected* graph — a directed graph's `addEdge` would only record the one direction.

## Space: proportional to actual connections

An adjacency list uses O(V + E) space — one list entry per vertex, plus one list element per edge (two, for an undirected graph, since each edge appears in both endpoints' lists). For a **sparse** graph — one where most vertices connect to only a few others, like a typical road network or social graph — this is far more memory-efficient than reserving space for every *possible* pair of vertices, which is exactly what Day 69's other representation does.
