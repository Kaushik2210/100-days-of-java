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
