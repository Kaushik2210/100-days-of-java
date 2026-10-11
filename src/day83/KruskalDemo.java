import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class KruskalDemo {

    public static void main(String[] args) {
        // the CLRS textbook graph: vertices a..i = 0..8
        String names = "abcdefghi";
        int[][] edges = {
            {0, 1, 4}, {0, 7, 8}, {1, 2, 8}, {1, 7, 11}, {2, 3, 7}, {2, 5, 4}, {2, 8, 2},
            {3, 4, 9}, {3, 5, 14}, {4, 5, 10}, {5, 6, 2}, {6, 7, 1}, {6, 8, 6}, {7, 8, 7}
        };

        List<int[]> chosen = new ArrayList<>();
        int total = kruskal(9, edges, chosen);
        System.out.println("MST total weight = " + total + " (known textbook answer: 37)");
        System.out.println("edges kept (" + chosen.size() + ", expected V - 1 = 8):");
        for (int[] edge : chosen) {
            System.out.println("  " + names.charAt(edge[0]) + " - " + names.charAt(edge[1]) + "  weight " + edge[2]);
        }
    }

    static int kruskal(int n, int[][] edges, List<int[]> chosen) {
        int[][] sorted = edges.clone();
        Arrays.sort(sorted, (a, b) -> Integer.compare(a[2], b[2]));

        UnionFind uf = new UnionFind(n);
        int total = 0;
        for (int[] edge : sorted) {
            if (uf.union(edge[0], edge[1])) {
                chosen.add(edge);
                total += edge[2];
                if (chosen.size() == n - 1) break;
            }
        }
        return total;
    }
}

class UnionFind {
    private int[] parent;
    private int[] size;

    UnionFind(int n) {
        parent = new int[n];
        size = new int[n];
        for (int i = 0; i < n; i++) {
            parent[i] = i;
            size[i] = 1;
        }
    }

    int find(int x) {
        if (parent[x] != x) {
            parent[x] = find(parent[x]);
        }
        return parent[x];
    }

    boolean union(int a, int b) {
        int rootA = find(a);
        int rootB = find(b);
        if (rootA == rootB) return false;

        if (size[rootA] < size[rootB]) {
            int temp = rootA;
            rootA = rootB;
            rootB = temp;
        }
        parent[rootB] = rootA;
        size[rootA] += size[rootB];
        return true;
    }
}
