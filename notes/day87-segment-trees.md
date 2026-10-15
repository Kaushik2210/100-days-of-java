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

## Range query

A query for `[ql, qr]` asks, at each node covering `[l, r]`, one of three questions:

1. **No overlap** (`qr < l` or `r < ql`): this segment contributes nothing. Return the identity value, `0` for a sum.
2. **Total overlap** (`ql ≤ l` and `r ≤ qr`): the whole segment is inside the query, so the node's stored value is exactly the answer for it. Return it without looking further down.
3. **Partial overlap**: the query covers some of this segment but not all. Ask both children and combine their answers.

```java
long query(int ql, int qr) { return query(1, 0, n - 1, ql, qr); }

private long query(int node, int l, int r, int ql, int qr) {
    if (qr < l || r < ql) return 0;                  // no overlap
    if (ql <= l && r <= qr) return tree[node];       // total overlap: use the precomputed sum
    int mid = (l + r) / 2;
    return query(2 * node, l, mid, ql, qr)           // partial overlap: split the question
         + query(2 * node + 1, mid + 1, r, ql, qr);
}
```

The query range is effectively decomposed into a small number of whole tree segments. At every level of the tree, at most two nodes can be only *partially* covered (the ones containing the query's two boundaries), and everything else either misses entirely or is covered entirely and stops immediately. That caps the work at a constant number of nodes per level, so a query is **O(log n)**.

## Why this is the right tool

Both operations touch only a thin slice of a balanced tree of height about `log₂ n`. With `n = 1,000,000` that is roughly 20 levels instead of a million elements, for both queries and updates. A segment tree is what you reach for when an array has to answer range questions *while it is also changing*.

## Beyond sums: any associative operation

Nothing above depends on the operation being addition. The combine step `left + right` can be replaced with any **associative** operation that has an identity value:

| Operation | Combine | Identity (for "no overlap") |
|---|---|---|
| Sum | `a + b` | `0` |
| Minimum | `Math.min(a, b)` | `Integer.MAX_VALUE` |
| Maximum | `Math.max(a, b)` | `Integer.MIN_VALUE` |
| GCD | `gcd(a, b)` | `0` |

Associativity matters because the tree regroups the elements: a query range is stitched together from node segments, so `(a ∘ b) ∘ c` must equal `a ∘ (b ∘ c)`. The operation does *not* need to be commutative as long as left-to-right order is respected, which the structure does naturally.

Passing the combine step in as a function (Day 29's functional interfaces) lets one class serve every one of these. A "range minimum query" tree is the same code with a different lambda and identity.

## Range updates and lazy propagation

The tree so far changes one element at a time. Suppose the update is "add `delta` to *every* element in `[ql, qr]`". Applying it as a loop of point updates costs O(n log n) in the worst case, which gives away the whole benefit.

**Lazy propagation** fixes this by *postponing* work. When an update fully covers a node's segment, the node's own sum is corrected right away (`sum += delta × segmentLength`), but the update is **not** pushed into the children. Instead the node records a pending `lazy` amount saying "my whole segment still owes this much to everything below me." That pending amount is only pushed down when a later operation actually has to look inside the node.

```java
private void apply(int node, int l, int r, long delta) {
    sum[node] += delta * (r - l + 1);   // correct this node's own total immediately
    lazy[node] += delta;                // remember that the children are behind by this much
}

private void push(int node, int l, int r) {
    if (lazy[node] == 0) return;
    int mid = (l + r) / 2;
    apply(2 * node, l, mid, lazy[node]);          // hand the debt to the children...
    apply(2 * node + 1, mid + 1, r, lazy[node]);
    lazy[node] = 0;                                // ...and clear it here
}

private void rangeAdd(int node, int l, int r, int ql, int qr, long delta) {
    if (qr < l || r < ql) return;                 // no overlap
    if (ql <= l && r <= qr) {                     // total overlap: update this node only, defer the rest
        apply(node, l, r, delta);
        return;
    }
    push(node, l, r);                              // partial overlap: children must be current before changing them
    int mid = (l + r) / 2;
    rangeAdd(2 * node, l, mid, ql, qr, delta);
    rangeAdd(2 * node + 1, mid + 1, r, ql, qr, delta);
    sum[node] = sum[2 * node] + sum[2 * node + 1];
}
```

`query` follows the same discipline: before descending into a partially covered node, call `push` so the children it reads are up to date.

With laziness, both range updates and range queries are **O(log n)**. The cost of the trick is extra memory (a second array) and extra care: forgetting a `push` before descending is the classic bug, and it produces answers that are wrong only for some combinations of operations.
