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
