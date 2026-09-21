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

## Traversals: visiting every node in a defined order

Unlike an array, a tree has no single obvious "first to last" order, so visiting every node means choosing one. There are three **depth-first** orders, differing only in *when* the current node is processed relative to its two subtrees, plus one **breadth-first** order.

```java
void preorder(TreeNode node) {   // node, then left, then right
    if (node == null) return;
    System.out.print(node.value + " ");
    preorder(node.left);
    preorder(node.right);
}

void inorder(TreeNode node) {    // left, then node, then right
    if (node == null) return;
    inorder(node.left);
    System.out.print(node.value + " ");
    inorder(node.right);
}

void postorder(TreeNode node) {  // left, then right, then node
    if (node == null) return;
    postorder(node.left);
    postorder(node.right);
    System.out.print(node.value + " ");
}
```

On the sample tree from above, these produce:

- **Preorder:** `1 2 4 5 3 6`
- **Inorder:** `4 2 5 1 3 6`
- **Postorder:** `4 5 2 6 3 1`

Each has real uses. **Preorder** processes a parent before its children, which suits copying or serializing (Day 35) a tree, since the root comes first. **Postorder** processes children before their parent, which suits deleting a tree or computing something that depends on a node's subtrees already being finished — `height` above is effectively a postorder computation. **Inorder** is special for binary *search* trees: it visits their values in sorted order, which Day 64 relies on directly.

## Level-order: breadth-first with a queue

Level-order traversal visits nodes row by row, top to bottom — all of depth 0, then depth 1, and so on. Recursion doesn't fit here, since the natural order cuts across subtrees. Instead it uses the queue from Day 61: enqueue the root, then repeatedly dequeue a node, process it, and enqueue its children.

```java
void levelOrder(TreeNode root) {
    if (root == null) return;
    java.util.Queue<TreeNode> queue = new java.util.LinkedList<>();
    queue.add(root);
    while (!queue.isEmpty()) {
        TreeNode node = queue.poll();
        System.out.print(node.value + " ");
        if (node.left != null) queue.add(node.left);
        if (node.right != null) queue.add(node.right);
    }
}
```

On the sample tree this prints `1 2 3 4 5 6`. This is the same breadth-first idea that Day 70 extends to graphs, and the same queue-versus-stack contrast: swap the queue for a stack and the traversal turns depth-first instead.

All four traversals are O(n) time, since every node is visited exactly once. The three depth-first ones use O(height) space for the recursion; level-order uses O(width) for the queue, where width is the most nodes on any single level.
