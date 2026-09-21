# Day 63: Trees — Binary Tree Basics & Traversals

Everything built so far — arrays, linked lists, stacks, queues — is **linear**: each element has at most one "next." A **tree** is hierarchical: each element can have several children, branching out like a family tree or a filesystem's directories. Trees are the foundation for binary search trees (Day 64), heaps (Day 66), tries (Day 68), and graphs (Day 69), which generalize trees further.

## Terminology

- **Node** — one element in the tree, holding a value and links to its children.
- **Root** — the single topmost node, with no parent.
- **Parent / child** — a node directly above / below another.
- **Leaf** — a node with no children.
- **Depth** of a node — the number of edges from the root down to it. The root has depth 0.
- **Height** of a tree — the number of edges on the longest path from the root down to a leaf. A tree with only a root has height 0.

A **binary tree** restricts every node to *at most two* children, called `left` and `right` — the shape used by most of the trees in this course.

## The node

The same linked structure as Day 58's list node, but with two forward references instead of one:

```java
class TreeNode {
    int value;
    TreeNode left;
    TreeNode right;

    TreeNode(int value) {
        this.value = value;
    }
}
```

Building a small tree is just wiring nodes together by hand:

```java
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
```

## Recursion fits trees naturally

A tree is itself a recursive structure: every node is the root of a smaller tree, its `left` and `right` subtrees. That makes recursion (Day 52) the natural tool — solve the problem for the left subtree, solve it for the right, and combine, with an empty subtree (`null`) as the base case.

```java
int size(TreeNode node) {
    if (node == null) return 0;                       // base case: an empty tree has no nodes
    return 1 + size(node.left) + size(node.right);    // this node, plus everything below it
}

int height(TreeNode node) {
    if (node == null) return -1;                      // so a single node (both children null) gets height 0
    return 1 + Math.max(height(node.left), height(node.right));
}

int countLeaves(TreeNode node) {
    if (node == null) return 0;
    if (node.left == null && node.right == null) return 1; // no children -- this node is a leaf
    return countLeaves(node.left) + countLeaves(node.right);
}
```

Each of these visits every node exactly once, so they're all O(n) time. Their space cost is the recursion depth (Day 42), which is O(height) — small for a balanced tree, but as large as O(n) for a badly lopsided one that's effectively a linked list.
