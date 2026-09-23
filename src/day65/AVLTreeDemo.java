public class AVLTreeDemo {

    public static void main(String[] args) {
        // build a small right-heavy chain by hand, then fix it with one rotation
        AVLNode root = new AVLNode(10);
        root.right = new AVLNode(20);
        root.height = 2;

        System.out.println("before rotation: balanceFactor(root) = " + balanceFactor(root));

        root.right.right = new AVLNode(30);
        root.right.height = 2;
        root.height = 3;
        System.out.println("after adding 30: balanceFactor(root) = " + balanceFactor(root) + " (too right-heavy)");

        AVLNode newRoot = rotateLeft(root);
        System.out.println("after rotateLeft: new root value = " + newRoot.value);
        System.out.println("balanceFactor(newRoot) = " + balanceFactor(newRoot));
        System.out.print("inorder (still sorted): ");
        inorder(newRoot);
        System.out.println();
    }

    static int height(AVLNode node) {
        return node == null ? 0 : node.height;
    }

    static int balanceFactor(AVLNode node) {
        return node == null ? 0 : height(node.left) - height(node.right);
    }

    static AVLNode rotateRight(AVLNode y) {
        AVLNode x = y.left;
        AVLNode t2 = x.right;

        x.right = y;
        y.left = t2;

        y.height = 1 + Math.max(height(y.left), height(y.right));
        x.height = 1 + Math.max(height(x.left), height(x.right));

        return x;
    }

    static AVLNode rotateLeft(AVLNode x) {
        AVLNode y = x.right;
        AVLNode t2 = y.left;

        y.left = x;
        x.right = t2;

        x.height = 1 + Math.max(height(x.left), height(x.right));
        y.height = 1 + Math.max(height(y.left), height(y.right));

        return y;
    }

    static void inorder(AVLNode node) {
        if (node == null) return;
        inorder(node.left);
        System.out.print(node.value + " ");
        inorder(node.right);
    }
}

class AVLNode {
    int value;
    int height = 1;
    AVLNode left;
    AVLNode right;

    AVLNode(int value) {
        this.value = value;
    }
}
