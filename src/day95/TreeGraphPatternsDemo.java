import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class TreeGraphPatternsDemo {

    static class TreeNode {
        int val;
        TreeNode left, right;

        TreeNode(int val) {
            this.val = val;
        }
    }

    // Build from a heap-indexed array: the children of index i are 2i + 1 and 2i + 2; null means no node.
    static TreeNode build(Integer[] level, int i) {
        if (i >= level.length || level[i] == null) return null;
        TreeNode node = new TreeNode(level[i]);
        node.left = build(level, 2 * i + 1);
        node.right = build(level, 2 * i + 2);
        return node;
    }

    // Grow a random tree by attaching each new node to a random free child slot.
    static TreeNode randomTree(Random random, int nodes) {
        TreeNode root = new TreeNode(random.nextInt(11) - 5);
        List<TreeNode> open = new ArrayList<>(List.of(root));
        for (int n = 1; n < nodes; n++) {
            TreeNode parent = open.get(random.nextInt(open.size()));
            TreeNode child = new TreeNode(random.nextInt(11) - 5);
            if (parent.left == null && (parent.right != null || random.nextBoolean())) parent.left = child;
            else parent.right = child;
            if (parent.left != null && parent.right != null) open.remove(parent);
            open.add(child);
        }
        return root;
    }

    static int count(TreeNode n) {
        return n == null ? 0 : 1 + count(n.left) + count(n.right);
    }

    static void preorder(TreeNode n, List<TreeNode> out) {
        if (n == null) return;
        out.add(n);
        preorder(n.left, out);
        preorder(n.right, out);
    }

    // Pattern 1: BFS by levels. Snapshot the queue size, then drain exactly one level per pass.
    static List<List<Integer>> levels(TreeNode root) {
        List<List<Integer>> result = new ArrayList<>();
        if (root == null) return result;
        Deque<TreeNode> queue = new ArrayDeque<>();
        queue.add(root);
        while (!queue.isEmpty()) {
            int width = queue.size();
            List<Integer> row = new ArrayList<>();
            for (int i = 0; i < width; i++) {
                TreeNode n = queue.poll();
                row.add(n.val);
                if (n.left != null) queue.add(n.left);
                if (n.right != null) queue.add(n.right);
            }
            result.add(row);
        }
        return result;
    }

    static int height(TreeNode n) {
        return n == null ? 0 : 1 + Math.max(height(n.left), height(n.right));
    }

    // Pattern 2: DFS that returns one thing and records another. depth() returns the height to its parent,
    // while the best path through each node (left depth + right depth) is recorded on the side.
    static int diameter(TreeNode root) {
        int[] best = {0};
        depth(root, best);
        return best[0];
    }

    static int depth(TreeNode n, int[] best) {
        if (n == null) return 0;
        int l = depth(n.left, best);
        int r = depth(n.right, best);
        best[0] = Math.max(best[0], l + r);
        return 1 + Math.max(l, r);
    }

    static int diameterBrute(TreeNode n) {
        if (n == null) return 0;
        int through = height(n.left) + height(n.right);
        return Math.max(through, Math.max(diameterBrute(n.left), diameterBrute(n.right)));
    }

    // Pattern 3: lowest common ancestor. If p and q are found in different subtrees, this node is the answer;
    // if only one side reports a hit, pass it up unchanged.
    static TreeNode lca(TreeNode root, TreeNode p, TreeNode q) {
        if (root == null || root == p || root == q) return root;
        TreeNode left = lca(root.left, p, q);
        TreeNode right = lca(root.right, p, q);
        if (left != null && right != null) return root;
        return left != null ? left : right;
    }

    static boolean pathTo(TreeNode n, TreeNode target, List<TreeNode> path) {
        if (n == null) return false;
        path.add(n);
        if (n == target || pathTo(n.left, target, path) || pathTo(n.right, target, path)) return true;
        path.remove(path.size() - 1);
        return false;
    }

    static TreeNode lcaBrute(TreeNode root, TreeNode p, TreeNode q) {
        List<TreeNode> a = new ArrayList<>(), b = new ArrayList<>();
        pathTo(root, p, a);
        pathTo(root, q, b);
        TreeNode last = null;
        for (int i = 0; i < Math.min(a.size(), b.size()) && a.get(i) == b.get(i); i++) last = a.get(i);
        return last;
    }

    // Pattern 4: prefix sums on a tree (day 94's pattern, one root-to-node path at a time). Count downward paths
    // summing to k. The map holds prefix sums of the current path only, so undo the entry when leaving a node.
    static int pathSums(TreeNode root, int k) {
        Map<Integer, Integer> seen = new HashMap<>();
        seen.put(0, 1);
        return pathSums(root, 0, k, seen);
    }

    static int pathSums(TreeNode n, int prefix, int k, Map<Integer, Integer> seen) {
        if (n == null) return 0;
        prefix += n.val;
        int found = seen.getOrDefault(prefix - k, 0);
        seen.merge(prefix, 1, Integer::sum);
        found += pathSums(n.left, prefix, k, seen) + pathSums(n.right, prefix, k, seen);
        seen.merge(prefix, -1, Integer::sum); // backtrack: siblings must not see this path's prefix
        return found;
    }

    static int pathSumsBrute(TreeNode root, int k) {
        if (root == null) return 0;
        return startingAt(root, 0, k) + pathSumsBrute(root.left, k) + pathSumsBrute(root.right, k);
    }

    static int startingAt(TreeNode n, int sum, int k) {
        if (n == null) return 0;
        sum += n.val;
        return (sum == k ? 1 : 0) + startingAt(n.left, sum, k) + startingAt(n.right, sum, k);
    }

    // Pattern 5: multi-source BFS. Seed the queue with every source at distance 0 and one sweep gives each
    // cell its distance to the nearest source. Walls are -1 in the grid; unreachable cells stay -1.
    static final int[][] STEPS = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};

    static int[][] nearestSource(int[][] walls, List<int[]> sources) {
        int rows = walls.length, cols = walls[0].length;
        int[][] dist = new int[rows][cols];
        for (int[] row : dist) Arrays.fill(row, -1);
        Deque<int[]> queue = new ArrayDeque<>();
        for (int[] s : sources) {
            dist[s[0]][s[1]] = 0;
            queue.add(s);
        }
        while (!queue.isEmpty()) {
            int[] cell = queue.poll();
            for (int[] step : STEPS) {
                int r = cell[0] + step[0], c = cell[1] + step[1];
                if (r < 0 || c < 0 || r >= rows || c >= cols || walls[r][c] == 1 || dist[r][c] != -1) continue;
                dist[r][c] = dist[cell[0]][cell[1]] + 1;
                queue.add(new int[] {r, c});
            }
        }
        return dist;
    }

    // Oracle: run an independent BFS from each source and keep the smaller distance per cell.
    static int[][] nearestSourceBrute(int[][] walls, List<int[]> sources) {
        int[][] best = null;
        for (int[] s : sources) {
            int[][] one = nearestSource(walls, List.of(s));
            if (best == null) best = one;
            else
                for (int r = 0; r < one.length; r++)
                    for (int c = 0; c < one[0].length; c++)
                        if (one[r][c] != -1 && (best[r][c] == -1 || one[r][c] < best[r][c])) best[r][c] = one[r][c];
        }
        return best;
    }

    // Pattern 6: flood fill to count connected components. An explicit stack avoids deep recursion on big grids.
    static int islands(int[][] grid) {
        int rows = grid.length, cols = grid[0].length, count = 0;
        boolean[][] seen = new boolean[rows][cols];
        for (int r = 0; r < rows; r++)
            for (int c = 0; c < cols; c++) {
                if (grid[r][c] != 1 || seen[r][c]) continue;
                count++;
                Deque<int[]> stack = new ArrayDeque<>();
                stack.push(new int[] {r, c});
                seen[r][c] = true;
                while (!stack.isEmpty()) {
                    int[] cell = stack.pop();
                    for (int[] step : STEPS) {
                        int nr = cell[0] + step[0], nc = cell[1] + step[1];
                        if (nr < 0 || nc < 0 || nr >= rows || nc >= cols || grid[nr][nc] != 1 || seen[nr][nc]) continue;
                        seen[nr][nc] = true;
                        stack.push(new int[] {nr, nc});
                    }
                }
            }
        return count;
    }

    // Oracle: union-find (day 80). Merge every pair of adjacent land cells, then count the roots.
    static int find(int[] parent, int x) {
        while (parent[x] != x) {
            parent[x] = parent[parent[x]];
            x = parent[x];
        }
        return x;
    }

    static int islandsUnionFind(int[][] grid) {
        int rows = grid.length, cols = grid[0].length;
        int[] parent = new int[rows * cols];
        for (int i = 0; i < parent.length; i++) parent[i] = i;
        for (int r = 0; r < rows; r++)
            for (int c = 0; c < cols; c++) {
                if (grid[r][c] != 1) continue;
                if (r + 1 < rows && grid[r + 1][c] == 1) parent[find(parent, r * cols + c)] = find(parent, (r + 1) * cols + c);
                if (c + 1 < cols && grid[r][c + 1] == 1) parent[find(parent, r * cols + c)] = find(parent, r * cols + c + 1);
            }
        int roots = 0;
        for (int r = 0; r < rows; r++)
            for (int c = 0; c < cols; c++)
                if (grid[r][c] == 1 && find(parent, r * cols + c) == r * cols + c) roots++;
        return roots;
    }

    // Pattern 7: ordering with dependencies. Kahn's algorithm repeatedly removes nodes with no unmet prerequisite;
    // if some nodes are never removed, they sit on a cycle and no valid order exists (returns null).
    static int[] topoOrder(int n, int[][] edges) {
        List<List<Integer>> next = new ArrayList<>();
        int[] indegree = new int[n];
        for (int i = 0; i < n; i++) next.add(new ArrayList<>());
        for (int[] e : edges) {
            next.get(e[0]).add(e[1]);
            indegree[e[1]]++;
        }
        Deque<Integer> ready = new ArrayDeque<>();
        for (int i = 0; i < n; i++) if (indegree[i] == 0) ready.add(i);
        int[] order = new int[n];
        int placed = 0;
        while (!ready.isEmpty()) {
            int u = ready.poll();
            order[placed++] = u;
            for (int v : next.get(u)) if (--indegree[v] == 0) ready.add(v);
        }
        return placed == n ? order : null;
    }

    // Oracle for "is there a cycle": DFS with three colors. Meeting a node that is still on the current path
    // (gray) means a back edge.
    static boolean hasCycle(int n, int[][] edges) {
        List<List<Integer>> next = new ArrayList<>();
        for (int i = 0; i < n; i++) next.add(new ArrayList<>());
        for (int[] e : edges) next.get(e[0]).add(e[1]);
        int[] color = new int[n]; // 0 = unvisited, 1 = on the current path, 2 = finished
        for (int i = 0; i < n; i++) if (color[i] == 0 && cycleFrom(i, next, color)) return true;
        return false;
    }

    static boolean cycleFrom(int u, List<List<Integer>> next, int[] color) {
        color[u] = 1;
        for (int v : next.get(u)) {
            if (color[v] == 1) return true;
            if (color[v] == 0 && cycleFrom(v, next, color)) return true;
        }
        color[u] = 2;
        return false;
    }

    static boolean respectsEdges(int[] order, int[][] edges) {
        int[] position = new int[order.length];
        for (int i = 0; i < order.length; i++) position[order[i]] = i;
        for (int[] e : edges) if (position[e[0]] >= position[e[1]]) return false;
        return true;
    }

    public static void main(String[] args) {
        TreeNode sample = build(new Integer[] {3, 9, 20, null, null, 15, 7}, 0);
        System.out.println("levels of [3, 9, 20, null, null, 15, 7] = " + levels(sample) + " (expected [[3], [9, 20], [15, 7]])");
        System.out.println("height = " + height(sample) + " (expected 3)");

        Random random = new Random(95); // fixed seed so the run is repeatable
        int trials = 2000;
        int bad = 0;
        for (int t = 0; t < trials; t++) {
            TreeNode root = randomTree(random, 1 + random.nextInt(12));
            List<List<Integer>> rows = levels(root);
            int total = 0;
            for (List<Integer> row : rows) total += row.size();
            if (total != count(root) || rows.size() != height(root)) bad++;
        }
        System.out.println("level-order rows cover every node and equal the height over " + trials + " random trees: "
            + (bad == 0 ? "all match" : bad + " MISMATCHES"));

        System.out.println("diameter (edges) of the sample tree = " + diameter(sample) + " (expected 3: 9 -> 3 -> 20 -> 15)");
        TreeNode skewed = build(new Integer[] {1, null, 2, null, null, null, 3}, 0);
        System.out.println("diameter of a right-leaning chain of 3 nodes = " + diameter(skewed) + " (expected 2)");
        System.out.println("diameter of a single node = " + diameter(new TreeNode(7)) + " (expected 0)");
        bad = 0;
        for (int t = 0; t < trials; t++) {
            TreeNode root = randomTree(random, 1 + random.nextInt(14));
            if (diameter(root) != diameterBrute(root)) bad++;
        }
        System.out.println("one-pass diameter vs the O(n^2) height-per-node version over " + trials + " random trees: "
            + (bad == 0 ? "all match" : bad + " MISMATCHES"));

        TreeNode s9 = sample.left, s20 = sample.right, s15 = sample.right.left, s7 = sample.right.right;
        System.out.println("lca(15, 7) = " + lca(sample, s15, s7).val + " (expected 20)");
        System.out.println("lca(9, 7) = " + lca(sample, s9, s7).val + " (expected 3)");
        System.out.println("lca(20, 7) = " + lca(sample, s20, s7).val + " (expected 20: a node can be its own ancestor)");
        bad = 0;
        int pairs = 0;
        for (int t = 0; t < trials; t++) {
            TreeNode root = randomTree(random, 1 + random.nextInt(14));
            List<TreeNode> nodes = new ArrayList<>();
            preorder(root, nodes);
            for (int k = 0; k < 5; k++) {
                TreeNode p = nodes.get(random.nextInt(nodes.size())), q = nodes.get(random.nextInt(nodes.size()));
                pairs++;
                if (lca(root, p, q) != lcaBrute(root, p, q)) bad++;
            }
        }
        System.out.println("recursive LCA vs comparing root-to-node paths over " + pairs + " random node pairs: "
            + (bad == 0 ? "all match" : bad + " MISMATCHES"));

        TreeNode sums = build(new Integer[] {10, 5, -3, 3, 2, null, 11, 3, -2, null, 1}, 0);
        System.out.println("downward paths summing to 8 in [10, 5, -3, 3, 2, null, 11, 3, -2, null, 1] = "
            + pathSums(sums, 8) + " (expected 3: 5 -> 3, 5 -> 2 -> 1, -3 -> 11)");
        bad = 0;
        int nonZero = 0;
        for (int t = 0; t < trials; t++) {
            TreeNode root = randomTree(random, 1 + random.nextInt(14));
            int k = random.nextInt(9) - 4;
            int expected = pathSumsBrute(root, k);
            if (pathSums(root, k) != expected) bad++;
            if (expected > 0) nonZero++;
        }
        System.out.println("prefix-map path sums vs starting a fresh sum at every node over " + trials + " random trees ("
            + nonZero + " with at least one path): " + (bad == 0 ? "all match" : bad + " MISMATCHES"));

        int[][] room = {
            {0, 0, 1, 0},
            {0, 0, 1, 0},
            {0, 0, 0, 0}
        };
        System.out.println("distance to the nearest of (0,0) and (0,3), walls in column 2:");
        for (int[] row : nearestSource(room, List.of(new int[] {0, 0}, new int[] {0, 3}))) System.out.println("  " + Arrays.toString(row));
        System.out.println("(expected: the wall column blocks the top two rows, and the bottom row reads [2, 3, 3, 2])");
        bad = 0;
        for (int t = 0; t < trials; t++) {
            int rows = 1 + random.nextInt(6), cols = 1 + random.nextInt(6);
            int[][] g = new int[rows][cols];
            for (int r = 0; r < rows; r++)
                for (int c = 0; c < cols; c++) g[r][c] = random.nextInt(100) < 25 ? 1 : 0;
            List<int[]> sources = new ArrayList<>();
            for (int r = 0; r < rows; r++)
                for (int c = 0; c < cols; c++)
                    if (g[r][c] == 0 && random.nextInt(100) < 20) sources.add(new int[] {r, c});
            if (sources.isEmpty()) continue;
            if (!Arrays.deepEquals(nearestSource(g, sources), nearestSourceBrute(g, sources))) bad++;
        }
        System.out.println("one multi-source BFS vs one BFS per source over " + trials + " random grids: "
            + (bad == 0 ? "all match" : bad + " MISMATCHES"));

        int[][] map = {
            {1, 1, 0, 0, 0},
            {1, 1, 0, 0, 0},
            {0, 0, 1, 0, 0},
            {0, 0, 0, 1, 1}
        };
        System.out.println("islands in the sample map = " + islands(map) + " (expected 3; diagonal cells do not connect)");
        bad = 0;
        for (int t = 0; t < trials; t++) {
            int rows = 1 + random.nextInt(8), cols = 1 + random.nextInt(8);
            int[][] g = new int[rows][cols];
            for (int r = 0; r < rows; r++)
                for (int c = 0; c < cols; c++) g[r][c] = random.nextInt(100) < 45 ? 1 : 0;
            if (islands(g) != islandsUnionFind(g)) bad++;
        }
        System.out.println("flood-fill islands vs union-find over " + trials + " random grids: "
            + (bad == 0 ? "all match" : bad + " MISMATCHES"));
        int[][] huge = new int[1500][1500];
        for (int[] row : huge) Arrays.fill(row, 1);
        System.out.println("1500x1500 all-land grid is one island: " + islands(huge) + " (explicit stack, no StackOverflowError)");

        int[][] courses = {{0, 1}, {0, 2}, {1, 3}, {2, 3}};
        System.out.println("order for edges 0->1, 0->2, 1->3, 2->3 = " + Arrays.toString(topoOrder(4, courses)) + " (expected [0, 1, 2, 3])");
        System.out.println("order with an added edge 3->0 = " + Arrays.toString(topoOrder(4, new int[][] {{0, 1}, {0, 2}, {1, 3}, {2, 3}, {3, 0}}))
            + " (expected null: 0 -> 1 -> 3 -> 0 is a cycle)");
        bad = 0;
        int cyclic = 0;
        for (int t = 0; t < trials; t++) {
            int n = 1 + random.nextInt(7);
            List<int[]> list = new ArrayList<>();
            int m = random.nextInt(2 * n);
            for (int i = 0; i < m; i++) {
                int u = random.nextInt(n), v = random.nextInt(n);
                list.add(new int[] {u, v});
            }
            int[][] edges = list.toArray(new int[0][]);
            int[] order = topoOrder(n, edges);
            boolean cycle = hasCycle(n, edges);
            if (cycle) cyclic++;
            if ((order == null) != cycle || (order != null && !respectsEdges(order, edges))) bad++;
        }
        System.out.println("Kahn's order vs three-color DFS cycle check over " + trials + " random digraphs (" + cyclic
            + " cyclic, orders also checked edge by edge): " + (bad == 0 ? "all match" : bad + " MISMATCHES"));
    }
}
