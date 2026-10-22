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
    }
}
