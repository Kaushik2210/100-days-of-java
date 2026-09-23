# Day 65: Balanced Trees — AVL Tree Rotations

Day 64 ended on a warning: a plain BST can degrade to O(n) height if values arrive in sorted order, losing every speed advantage a tree was supposed to give. An **AVL tree** (named for its inventors, Adelson-Velsky and Landis) is a BST that actively repairs its own shape after every insert, guaranteeing O(log n) height no matter what order values arrive in.

## Tracking height and balance factor

Every AVL node stores its own subtree height, updated as changes propagate back up the recursion — the same postorder-style bottom-up computation as Day 63's `height` function, but cached per node instead of recomputed from scratch each time.

```java
class AVLNode {
    int value;
    int height = 1; // a new leaf has height 1
    AVLNode left;
    AVLNode right;

    AVLNode(int value) {
        this.value = value;
    }
}

int height(AVLNode node) {
    return node == null ? 0 : node.height;
}

int balanceFactor(AVLNode node) {
    return node == null ? 0 : height(node.left) - height(node.right);
}
```

The **balance factor** is left height minus right height. An AVL tree keeps every node's balance factor within `{-1, 0, 1}` — left-heavy by at most one level, or right-heavy by at most one level, never more. The moment an insert pushes a node's balance factor to `-2` or `2`, that's the signal a rotation is needed.

## Rotations: restructuring without breaking the BST property

A rotation re-parents a small group of nodes to shift height from one side to the other, while preserving every node's left/right ordering (Day 64's BST invariant is never violated — only the *shape*, never the *values*, changes).

```java
AVLNode rotateRight(AVLNode y) {
    AVLNode x = y.left;
    AVLNode t2 = x.right;

    x.right = y;   // y becomes x's right child
    y.left = t2;   // y adopts x's old right subtree (still valid: everything in t2 is > x, < y)

    y.height = 1 + Math.max(height(y.left), height(y.right)); // update heights, child first
    x.height = 1 + Math.max(height(x.left), height(x.right));

    return x; // x is the new root of this subtree
}

AVLNode rotateLeft(AVLNode x) {
    AVLNode y = x.right;
    AVLNode t2 = y.left;

    y.left = x;
    x.right = t2;

    x.height = 1 + Math.max(height(x.left), height(x.right));
    y.height = 1 + Math.max(height(y.left), height(y.right));

    return y;
}
```

Both are O(1) — only a handful of pointers and two height updates change, regardless of how large the subtrees involved are. `rotateRight` and `rotateLeft` are mirror images of each other.
