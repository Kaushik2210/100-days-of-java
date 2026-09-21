public class BinaryTreeDemo {

    public static void main(String[] args) {
        //         1
        //        / \
        //       2   3
        //      / \   \
        //     4   5   6
        TreeNode root = new TreeNode(1);
        root.left = new TreeNode(2);
        root.right = new TreeNode(3);
        root.left.left = new TreeNode(4);
        root.left.right = new TreeNode(5);
        root.right.right = new TreeNode(6);

        System.out.println("size = " + size(root));
        System.out.println("height = " + height(root));
        System.out.println("leaves = " + countLeaves(root));

        TreeNode single = new TreeNode(42);
        System.out.println("single-node tree height = " + height(single));
        System.out.println("empty tree size = " + size(null));
    }

    static int size(TreeNode node) {
        if (node == null) return 0;
        return 1 + size(node.left) + size(node.right);
    }

    static int height(TreeNode node) {
        if (node == null) return -1;
        return 1 + Math.max(height(node.left), height(node.right));
    }

    static int countLeaves(TreeNode node) {
        if (node == null) return 0;
        if (node.left == null && node.right == null) return 1;
        return countLeaves(node.left) + countLeaves(node.right);
    }
}

class TreeNode {
    int value;
    TreeNode left;
    TreeNode right;

    TreeNode(int value) {
        this.value = value;
    }
}
