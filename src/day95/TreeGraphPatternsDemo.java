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
    }
}
