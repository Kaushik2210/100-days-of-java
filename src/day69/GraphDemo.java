import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GraphDemo {

    public static void main(String[] args) {
        Graph graph = new Graph();
        graph.addEdge(1, 2);
        graph.addEdge(1, 3);
        graph.addEdge(2, 4);
        graph.addEdge(3, 4);

        for (int vertex = 1; vertex <= 4; vertex++) {
            System.out.println("neighborsOf(" + vertex + ") = " + graph.neighborsOf(vertex));
        }

        System.out.println();
        System.out.println("Same graph as an adjacency matrix (vertices 0..4, index 0 unused for readability):");
        MatrixGraph matrixGraph = new MatrixGraph(5);
        matrixGraph.addEdge(1, 2);
        matrixGraph.addEdge(1, 3);
        matrixGraph.addEdge(2, 4);
        matrixGraph.addEdge(3, 4);

        System.out.println("hasEdge(1, 2) = " + matrixGraph.hasEdge(1, 2));
        System.out.println("hasEdge(1, 4) = " + matrixGraph.hasEdge(1, 4));
        for (int vertex = 1; vertex <= 4; vertex++) {
            System.out.println("neighborsOf(" + vertex + ") = " + matrixGraph.neighborsOf(vertex));
        }
    }
}

class MatrixGraph {
    private boolean[][] matrix;
    private int vertexCount;

    MatrixGraph(int vertexCount) {
        this.vertexCount = vertexCount;
        matrix = new boolean[vertexCount][vertexCount];
    }

    void addEdge(int from, int to) {
        matrix[from][to] = true;
        matrix[to][from] = true;
    }

    boolean hasEdge(int from, int to) {
        return matrix[from][to];
    }

    List<Integer> neighborsOf(int vertex) {
        List<Integer> neighbors = new ArrayList<>();
        for (int i = 0; i < vertexCount; i++) {
            if (matrix[vertex][i]) neighbors.add(i);
        }
        return neighbors;
    }
}

class Graph {
    private Map<Integer, List<Integer>> adjacencyList = new HashMap<>();

    void addVertex(int vertex) {
        adjacencyList.putIfAbsent(vertex, new ArrayList<>());
    }

    void addEdge(int from, int to) {
        addVertex(from);
        addVertex(to);
        adjacencyList.get(from).add(to);
        adjacencyList.get(to).add(from);
    }

    List<Integer> neighborsOf(int vertex) {
        return adjacencyList.getOrDefault(vertex, new ArrayList<>());
    }
}
