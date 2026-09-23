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

        System.out.println();
        System.out.println("Inserting 1..15 in already-sorted order (Day 64's worst case for a plain BST):");
        AVLNode avlRoot = null;
        for (int i = 1; i <= 15; i++) {
            avlRoot = insert(avlRoot, i);
        }
        System.out.println("AVL tree height with 15 sorted inserts: " + height(avlRoot) + " (log2(15) ~ 3.9)");
        System.out.print("inorder (still sorted): ");
        inorder(avlRoot);
        System.out.println();

        BSTNode plainRoot = null;
        for (int i = 1; i <= 15; i++) {
            plainRoot = plainInsert(plainRoot, i);
        }
        System.out.println("plain BST height with 15 sorted inserts: " + plainHeight(plainRoot)
            + " (degenerated into a linked list)");
    }

    static AVLNode insert(AVLNode node, int value) {
        if (node == null) return new AVLNode(value);

        if (value < node.value) {
            node.left = insert(node.left, value);
        } else if (value > node.value) {
            node.right = insert(node.right, value);
        } else {
            return node;
        }

        node.height = 1 + Math.max(height(node.left), height(node.right));
        int balance = balanceFactor(node);

        if (balance > 1 && value < node.left.value) return rotateRight(node);
        if (balance < -1 && value > node.right.value) return rotateLeft(node);
        if (balance > 1 && value > node.left.value) {
            node.left = rotateLeft(node.left);
            return rotateRight(node);
        }
        if (balance < -1 && value < node.right.value) {
            node.right = rotateRight(node.right);
            return rotateLeft(node);
        }

        return node;
    }

    static BSTNode plainInsert(BSTNode node, int value) {
        if (node == null) return new BSTNode(value);
        if (value < node.value) node.left = plainInsert(node.left, value);
        else if (value > node.value) node.right = plainInsert(node.right, value);
        return node;
    }

    static int plainHeight(BSTNode node) {
        return node == null ? -1 : 1 + Math.max(plainHeight(node.left), plainHeight(node.right));
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

class BSTNode {
    int value;
    BSTNode left;
    BSTNode right;

    BSTNode(int value) {
        this.value = value;
    }
}
