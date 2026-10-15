# Day 87: Advanced Trees — Segment Trees

Day 72's sliding window answered questions about a range of an array when the range moved smoothly. Many problems instead throw an arbitrary stream of requests at an array: "what is the sum of elements `l..r`?" interleaved with "change element `i` to this value." A **segment tree** supports both operations in O(log n).

## The problem it solves

Consider the two obvious approaches:

| Approach | Range query | Point update |
|---|---|---|
| Just scan the array | O(n) | O(1) |
| Prefix-sum array | O(1) | O(n) (every later prefix changes) |

Each makes one operation cheap by making the other expensive. When queries and updates are both frequent and mixed, either approach degrades to O(n) per request. A segment tree balances the two so both cost O(log n).

## The structure

A segment tree is a binary tree in which each node stores the answer (here, the sum) for a *segment* of the array:

- The **root** covers the whole array `[0, n-1]`.
- Each node covering `[l, r]` has a left child covering `[l, mid]` and a right child covering `[mid+1, r]`.
- **Leaves** cover a single element.
- Each internal node's value is built from its two children's values, here `left + right`.

Like Day 66's heap, it lives in a plain array with no node objects. Using 1-based numbering, the root is index `1` and the children of node `i` are `2i` and `2i + 1`. Because the shape depends on the array length rather than being perfectly packed, the backing array is sized `4n`, which is always enough.

```java
class SegmentTree {
    private final int n;
    private final long[] tree;

    SegmentTree(int[] data) {
        n = data.length;
        tree = new long[4 * Math.max(1, n)];
        if (n > 0) build(data, 1, 0, n - 1);
    }

    private void build(int[] data, int node, int l, int r) {
        if (l == r) {                       // a leaf holds one array element
            tree[node] = data[l];
            return;
        }
        int mid = (l + r) / 2;
        build(data, 2 * node, l, mid);
        build(data, 2 * node + 1, mid + 1, r);
        tree[node] = tree[2 * node] + tree[2 * node + 1]; // combine the two halves
    }
}
```

Building touches every node once. There are about `2n` nodes, so construction is **O(n)**.

## Point update

To change `data[idx]`, walk from the root down to that element's leaf, set it, and then recompute each ancestor on the way back up. Only one root-to-leaf path is affected, and the tree's height is about `log₂ n`.

```java
void update(int idx, int value) { update(1, 0, n - 1, idx, value); }

private void update(int node, int l, int r, int idx, int value) {
    if (l == r) {
        tree[node] = value;
        return;
    }
    int mid = (l + r) / 2;
    if (idx <= mid) update(2 * node, l, mid, idx, value);
    else update(2 * node + 1, mid + 1, r, idx, value);
    tree[node] = tree[2 * node] + tree[2 * node + 1]; // refresh this ancestor from its updated child
}
```

This is **O(log n)**.
