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

## The four imbalance shapes

An insert can throw a node's balance factor out of range in four distinct shapes, and each needs a different fix:

- **LL (left-left)** — the new node landed in the left subtree of the left child. Fixed with a single `rotateRight` on the unbalanced node.
- **RR (right-right)** — mirror of LL, in the right subtree of the right child. Fixed with a single `rotateLeft`.
- **LR (left-right)** — the new node landed in the *right* subtree of the left child — a "zigzag." A single rotation can't fix this directly; first `rotateLeft` on the left child to turn it into an LL shape, then `rotateRight` on the original node.
- **RL (right-left)** — mirror of LR: `rotateRight` on the right child first, then `rotateLeft` on the original node.

## Insert with rebalancing

Insertion follows Day 64's BST insert exactly, then — on the way back up the recursion — updates each node's height and checks its balance factor, applying the appropriate rotation if it's out of range.

```java
AVLNode insert(AVLNode node, int value) {
    if (node == null) return new AVLNode(value);

    if (value < node.value) {
        node.left = insert(node.left, value);
    } else if (value > node.value) {
        node.right = insert(node.right, value);
    } else {
        return node; // duplicate -- ignore
    }

    node.height = 1 + Math.max(height(node.left), height(node.right));
    int balance = balanceFactor(node);

    if (balance > 1 && value < node.left.value) return rotateRight(node);                    // LL
    if (balance < -1 && value > node.right.value) return rotateLeft(node);                    // RR
    if (balance > 1 && value > node.left.value) {                                             // LR
        node.left = rotateLeft(node.left);
        return rotateRight(node);
    }
    if (balance < -1 && value < node.right.value) {                                           // RL
        node.right = rotateRight(node.right);
        return rotateLeft(node);
    }

    return node; // already balanced -- no rotation needed
}
```

Because rebalancing happens after *every single insert*, the tree can never drift more than one level out of balance in the first place — there's no accumulated damage to repair later, only ever a local, O(1) fix at the specific node where the imbalance first appears. This is what guarantees O(log n) height (and therefore O(log n) insert/search/delete) no matter what order values arrive in — the exact failure mode Day 64 identified in a plain BST.
