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
