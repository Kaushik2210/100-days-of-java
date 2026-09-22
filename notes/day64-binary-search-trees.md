# Day 64: Binary Search Trees — Insert, Search, Delete

Day 63's binary tree had no rule about where values go. A **binary search tree (BST)** adds exactly one: for every node, everything in its left subtree is smaller, and everything in its right subtree is larger. That single invariant is what makes search fast — the same "eliminate half the remaining space" idea as binary search (Day 53), except walking tree links instead of narrowing array indices.

## The BST property

```java
class BSTNode {
    int value;
    BSTNode left;
    BSTNode right;

    BSTNode(int value) {
        this.value = value;
    }
}
```

For every node: `node.left`'s entire subtree holds values `< node.value`, and `node.right`'s entire subtree holds values `> node.value` — not just its immediate children, the *whole* subtree.

## Insert

Insertion walks down from the root, going left or right based on the comparison, until it finds the empty spot (`null`) where the new value belongs — then attaches a new node there.

```java
BSTNode insert(BSTNode node, int value) {
    if (node == null) return new BSTNode(value); // found the empty spot -- attach here
    if (value < node.value) {
        node.left = insert(node.left, value);   // recurse left, then reattach the (possibly new) subtree
    } else if (value > node.value) {
        node.right = insert(node.right, value);
    }
    // equal values are ignored here -- a BST commonly either rejects or allows duplicates by convention
    return node;
}
```

Returning `node` (or the newly created node) at every level and reassigning it (`node.left = insert(node.left, value)`) is the same pattern Day 58's `addLast`/Day 52's recursion used — it's how a recursive function "edits" a structure built of `null`-terminated links without needing a separate reference parameter.

## Search

Search follows the identical left/right decision as insert, just without ever creating a node — and stops the moment it finds a match or falls off the tree (`null`).

```java
boolean search(BSTNode node, int value) {
    if (node == null) return false;      // fell off the tree -- not present
    if (value == node.value) return true;
    return value < node.value ? search(node.left, value) : search(node.right, value);
}
```

## Why O(log n) — and when it isn't

Both `insert` and `search` do O(1) work per level and only ever go one direction, so their cost is O(height) — Day 63's height, not the node count directly. For a **balanced** tree, height is O(log n), giving the same speed as binary search. But nothing about a plain BST *enforces* balance: inserting already-sorted values (1, 2, 3, 4, 5...) produces a tree that's really just a right-leaning linked list, with height O(n) — degrading every operation to O(n), no better than Day 58's list. Day 65's AVL tree fixes this by actively rebalancing after every insert.

## Delete: three cases

Deleting a node has to preserve the BST property for every node that's left behind, and the fix differs depending on how many children the deleted node has.

**Case 1 — no children (a leaf).** Just detach it; nothing else needs to change.

**Case 2 — one child.** Replace the node with its one child — the parent simply skips over the deleted node, exactly like Day 58's singly-linked-list `delete`.

**Case 3 — two children.** There's no single child to promote without breaking the ordering. The fix: find the node's **inorder successor** — the smallest value in its right subtree (equivalently, the next value that would appear after it in an inorder traversal) — copy that value into the node being "deleted," then delete the successor from the right subtree instead. The successor is guaranteed to have at most one child (it's the leftmost node of the right subtree, so it can only have a right child, never a left one), so removing *it* only ever needs Case 1 or Case 2.

```java
BSTNode delete(BSTNode node, int value) {
    if (node == null) return null; // value not found -- nothing to delete

    if (value < node.value) {
        node.left = delete(node.left, value);
    } else if (value > node.value) {
        node.right = delete(node.right, value);
    } else {
        // found the node to delete
        if (node.left == null) return node.right;   // Case 1 (no children, returns null) or Case 2 (one right child)
        if (node.right == null) return node.left;    // Case 2 (one left child)

        // Case 3: two children -- find the inorder successor (smallest value in the right subtree)
        BSTNode successor = node.right;
        while (successor.left != null) {
            successor = successor.left;
        }
        node.value = successor.value;                 // copy the successor's value up
        node.right = delete(node.right, successor.value); // then remove the successor from the right subtree
    }
    return node;
}
```

Case 1 and Case 2 collapse into the same two lines (`if (node.left == null) return node.right;` handles both — when there's no left child, returning `node.right` returns either the single child or `null` if there were no children at all). This mirrors Day 58's linked-list delete conceptually, but a BST delete additionally has to keep the ordering invariant intact, which is what makes Case 3 necessary in the first place.
