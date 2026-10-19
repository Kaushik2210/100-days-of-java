import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

public class MatrixDemo {

    static final int[] dr = {-1, 1, 0, 0};
    static final int[] dc = {0, 0, -1, 1};

    public static void main(String[] args) {
        int[][] islandGrid = {
            {1, 1, 0, 0, 0},
            {1, 1, 0, 0, 0},
            {0, 0, 1, 0, 0},
            {0, 0, 0, 1, 1}
        };
        System.out.println("islands = " + countIslands(islandGrid)
            + " (expected 3: the 2x2 block, the lone cell, and the right-hand pair; diagonal neighbors don't connect)");
        System.out.println("original grid left untouched by the search: " + Arrays.deepToString(islandGrid));

        System.out.println();
        int[][] maze = {
            {0, 0, 0, 0},
            {1, 1, 0, 1},
            {0, 0, 0, 0},
            {0, 1, 1, 0}
        };
        System.out.println("shortest path through the maze = " + shortestPath(maze) + " steps (expected 6)");
        int[][] blocked = {
            {0, 1},
            {1, 0}
        };
        System.out.println("shortest path through a fully walled-off maze = " + shortestPath(blocked) + " (expected -1)");
    }

    static int countIslands(int[][] original) {
        int rows = original.length;
        int cols = rows == 0 ? 0 : original[0].length;
        int[][] grid = new int[rows][];
        for (int r = 0; r < rows; r++) grid[r] = original[r].clone();

        int islands = 0;
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (grid[r][c] == 1) {
                    islands++;
                    sink(grid, r, c);
                }
            }
        }
        return islands;
    }

    static void sink(int[][] grid, int r, int c) {
        if (r < 0 || r >= grid.length || c < 0 || c >= grid[0].length || grid[r][c] != 1) return;
        grid[r][c] = 0;
        sink(grid, r - 1, c);
        sink(grid, r + 1, c);
        sink(grid, r, c - 1);
        sink(grid, r, c + 1);
    }

    static int shortestPath(int[][] maze) {
        int rows = maze.length;
        int cols = maze[0].length;
        if (maze[0][0] == 1 || maze[rows - 1][cols - 1] == 1) return -1;

        int[][] dist = new int[rows][cols];
        for (int[] row : dist) Arrays.fill(row, -1);
        Queue<int[]> queue = new LinkedList<>();
        dist[0][0] = 0;
        queue.add(new int[]{0, 0});

        while (!queue.isEmpty()) {
            int[] cell = queue.poll();
            int r = cell[0], c = cell[1];
            if (r == rows - 1 && c == cols - 1) return dist[r][c];
            for (int d = 0; d < 4; d++) {
                int nr = r + dr[d], nc = c + dc[d];
                if (nr < 0 || nr >= rows || nc < 0 || nc >= cols) continue;
                if (maze[nr][nc] == 1 || dist[nr][nc] != -1) continue;
                dist[nr][nc] = dist[r][c] + 1;
                queue.add(new int[]{nr, nc});
            }
        }
        return -1;
    }
}
