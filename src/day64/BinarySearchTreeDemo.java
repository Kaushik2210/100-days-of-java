public class BinarySearchTreeDemo {

    public static void main(String[] args) {
        BSTNode root = null;
        for (int value : new int[]{50, 30, 70, 20, 40, 60, 80}) {
            root = insert(root, value);
        }

        System.out.print("inorder (should be sorted): ");
        inorder(root);
        System.out.println();

        System.out.println("search(40) = " + search(root, 40));
        System.out.println("search(99) = " + search(root, 99));

        // tree is currently:
        //             50
        //           /    \
        //         30      70
        //        /  \    /  \
        //      20   40  60  80

        System.out.println();
        root = delete(root, 20); // Case 1: leaf
        System.out.print("after deleting leaf 20:        ");
        inorder(root);
        System.out.println();

        root = delete(root, 30); // Case 2: one child remaining (40) after 20 is already gone
        System.out.print("after deleting 30 (one child): ");
        inorder(root);
        System.out.println();

        root = delete(root, 70); // Case 3: two children (60, 80) -- inorder successor is 80
        System.out.print("after deleting 70 (two children): ");
        inorder(root);
        System.out.println();

        System.out.println("search(70) = " + search(root, 70) + " (correctly gone)");
        System.out.println("search(80) = " + search(root, 80) + " (successor, still present)");
    }

    static BSTNode insert(BSTNode node, int value) {
        if (node == null) return new BSTNode(value);
        if (value < node.value) {
            node.left = insert(node.left, value);
        } else if (value > node.value) {
            node.right = insert(node.right, value);
        }
        return node;
    }

    static boolean search(BSTNode node, int value) {
        if (node == null) return false;
        if (value == node.value) return true;
        return value < node.value ? search(node.left, value) : search(node.right, value);
    }

    static BSTNode delete(BSTNode node, int value) {
        if (node == null) return null;

        if (value < node.value) {
            node.left = delete(node.left, value);
        } else if (value > node.value) {
            node.right = delete(node.right, value);
        } else {
            if (node.left == null) return node.right;
            if (node.right == null) return node.left;

            BSTNode successor = node.right;
            while (successor.left != null) {
                successor = successor.left;
            }
            node.value = successor.value;
            node.right = delete(node.right, successor.value);
        }
        return node;
    }

    static void inorder(BSTNode node) {
        if (node == null) return;
        inorder(node.left);
        System.out.print(node.value + " ");
        inorder(node.right);
    }
}

class BSTNode {
    int value;
    BSTNode left;
    BSTNode right;

    BSTNode(int value) {
        this.value = value;
    }
}
