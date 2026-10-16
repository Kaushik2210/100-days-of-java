# Day 88: Advanced Trees — Fenwick Trees (Binary Indexed Trees)

Day 87's segment tree handles range queries and updates in O(log n), but it carries a lot of machinery: a `4n` array, recursion, and three overlap cases. For the very common special case of **prefix sums with updates**, there is a much smaller structure. The **Fenwick tree** (also called a **binary indexed tree**, or BIT) does it with a single array of `n + 1` numbers and a few lines of loop code, and it leans on Day 79's `n & -n` trick.

## The key idea: each cell covers a power-of-two-length stretch

Use 1-based indexing. Let `lowbit(i) = i & -i`, the value of the lowest set bit of `i` (Day 79). Cell `tree[i]` stores the sum of the `lowbit(i)` elements that **end at position `i`**, that is, the range `(i - lowbit(i), i]`.

For `n = 8`:

| Cell | `lowbit` | Covers (1-based) |
|---|---|---|
| `tree[1]` | 1 | `a1` |
| `tree[2]` | 2 | `a1..a2` |
| `tree[3]` | 1 | `a3` |
| `tree[4]` | 4 | `a1..a4` |
| `tree[5]` | 1 | `a5` |
| `tree[6]` | 2 | `a5..a6` |
| `tree[7]` | 1 | `a7` |
| `tree[8]` | 8 | `a1..a8` |

Nothing is stored in a tree shape; the "tree" is implied by the index arithmetic.

## Prefix sum query

To get the sum of the first `i` elements, add `tree[i]`, then jump to the next uncovered prefix by **removing the lowest set bit** (`i -= i & -i`), and repeat until `i` reaches `0`.

For `i = 7` (binary `111`): `tree[7]` covers `a7`, then `i` becomes `6` (`110`), `tree[6]` covers `a5..a6`, then `i` becomes `4` (`100`), `tree[4]` covers `a1..a4`. Together that is `a1..a7`, with no overlap and no gap.

```java
long prefixSum(int i) {          // sum of elements 1..i
    long sum = 0;
    while (i > 0) {
        sum += tree[i];
        i -= i & -i;             // drop the lowest set bit: jump to the next block
    }
    return sum;
}
```

Each step clears one set bit, and a number up to `n` has at most about `log₂ n` set bits, so a query is **O(log n)**.

## Point update

Changing `a_i` by `delta` must refresh every cell whose range includes position `i`. Those cells are found by moving the **other** way: add the lowest set bit (`i += i & -i`) until the index runs past `n`.

```java
void add(int i, long delta) {    // a_i += delta
    while (i <= n) {
        tree[i] += delta;
        i += i & -i;             // jump to the next cell whose range also contains position i
    }
}
```

Adding the lowest set bit makes the lowest set bit climb to a higher position each time, so this also takes **O(log n)** steps.

## The whole class

```java
class FenwickTree {
    private final int n;
    private final long[] tree;

    FenwickTree(int n) {
        this.n = n;
        this.tree = new long[n + 1];   // index 0 is unused
    }
    // add(...) and prefixSum(...) as above
}
```

Compared with Day 87's `4n`-sized array, this uses `n + 1` cells, and the loops are tiny, so in practice Fenwick trees are also noticeably faster for the problems they fit.

## Range sums come from subtraction

The tree only answers *prefix* sums directly. A sum over `[l, r]` is a difference of two prefixes:

```java
long rangeSum(int l, int r) {
    return prefixSum(r) - prefixSum(l - 1);
}
```

This is the point where Fenwick trees are less general than segment trees. Subtraction has to undo addition, so the operation must be **invertible**. Sums, XOR, and counts qualify. **Minimum and maximum do not**: knowing the minimum of the first `r` elements and of the first `l - 1` elements tells you nothing about the minimum of the stretch between them. For min, max, gcd, or anything non-invertible, use a segment tree.

## "Set" versus "add"

`add(i, delta)` adds to a position. To *assign* a new value, keep a plain copy of the array and add the difference: `add(i, newValue - old[i])`, then store `old[i] = newValue`.

## Building in O(n)

Starting from an array and calling `add` for every element costs O(n log n). A linear build pushes each cell's finished total straight into its single "parent" cell:

```java
FenwickTree(long[] a) {              // a is 1-based; a[0] is ignored
    n = a.length - 1;
    tree = new long[n + 1];
    for (int i = 1; i <= n; i++) {
        tree[i] += a[i];
        int parent = i + (i & -i);   // the next cell whose range contains this one
        if (parent <= n) tree[parent] += tree[i];
    }
}
```

Processing indices in increasing order guarantees `tree[i]` is complete before it is added into its parent.

## Range updates: Fenwick over differences

A related question is "add `x` to every element in `[l, r]`, then answer **point** queries." Store the **difference array** `d[i] = a[i] - a[i-1]` in the Fenwick tree. A range add touches just two differences (`d[l] += x`, `d[r+1] -= x`), and the value of any single element is the prefix sum of `d` up to it.

Supporting range updates **and** range sums takes two Fenwick trees. Using `a[k] = d[1] + ... + d[k]`, the prefix sum of the array is

`S(i) = Σ_{j≤i} d[j] · (i − j + 1) = (i + 1) · Σ d[j] − Σ j · d[j]`

so keep one tree `B1` over `d[j]` and one tree `B2` over `j · d[j]`:

```java
void rangeAdd(int l, int r, long x) {
    add(b1, l, x);          add(b1, r + 1, -x);
    add(b2, l, x * l);      add(b2, r + 1, -x * (r + 1));
}

long prefix(int i) {
    return (i + 1) * prefixSum(b1, i) - prefixSum(b2, i);
}

long rangeSum(int l, int r) { return prefix(r) - prefix(l - 1); }
```

This gives the same capability as Day 87's lazy segment tree in a fraction of the code, with the same O(log n) cost.

## A classic application: counting inversions

An **inversion** is a pair `i < j` with `a[i] > a[j]`. Sweeping left to right and using a Fenwick tree as a frequency table over the values lets each element ask "how many earlier elements were larger than me?" in O(log n):

```java
long countInversions(int[] a, int maxValue) {      // values must lie in 1..maxValue
    FenwickTree seen = new FenwickTree(maxValue);
    long inversions = 0;
    for (int i = 0; i < a.length; i++) {
        inversions += i - seen.prefixSum(a[i]);     // earlier elements minus those <= a[i] = those > a[i]
        seen.add(a[i], 1);
    }
    return inversions;
}
```

That counts all inversions in **O(n log n)** total, compared with the O(n²) pair-by-pair check, and it is also the number of swaps Day 54's bubble sort would perform.
