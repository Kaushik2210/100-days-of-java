# Day 66: Heaps — Binary Heap Implementation (Min-Heap & Max-Heap)

Day 57 used a heap internally to power heap sort. Day 62 leaned on `java.util.PriorityQueue` without looking inside it. Today builds the same structure as a standalone, resizable data structure — the thing both of those were built on.

## A heap is a complete binary tree, stored as an array

A **min-heap** keeps one invariant: every parent is less than or equal to both its children — so the smallest element is always at the root. (A max-heap is the mirror image, largest at the root.) Critically, a heap is always a **complete** binary tree — every level is fully filled except possibly the last, which fills left to right with no gaps — and that completeness is exactly what lets it live in a plain array with no `left`/`right` pointers at all, the same arithmetic Day 57 used: node `i`'s children sit at `2i + 1` and `2i + 2`, its parent at `(i - 1) / 2`.

```java
class MinHeap {
    private int[] data;
    private int size = 0;

    MinHeap(int capacity) {
        data = new int[capacity];
    }

    boolean isEmpty() {
        return size == 0;
    }

    int peek() {
        if (isEmpty()) throw new IllegalStateException("Heap is empty");
        return data[0]; // the minimum is always the root
    }
}
```

## Insert: add at the end, then sift up

A new element always goes into the next open array slot — keeping the tree complete — then "sifts up," swapping with its parent as long as it's smaller, until the heap property holds again.

```java
void insert(int value) {
    if (size == data.length) throw new IllegalStateException("Heap is full");
    data[size] = value; // append at the next open slot -- stays complete
    siftUp(size);
    size++;
}

private void siftUp(int i) {
    while (i > 0) {
        int parent = (i - 1) / 2;
        if (data[parent] <= data[i]) break; // parent already smaller or equal -- heap property holds
        swap(parent, i);
        i = parent; // continue checking from the new position
    }
}

private void swap(int a, int b) {
    int temp = data[a];
    data[a] = data[b];
    data[b] = temp;
}
```

Since the tree is complete, its height is always O(log n) regardless of insertion order — unlike Day 64's plain BST, there's no degenerate shape to worry about, because the array-slot-filling rule leaves no room for one. `siftUp` walks at most that height, so `insert` is O(log n) worst case, guaranteed.

## Extract: remove the root, then sift down

The whole point of a heap is O(log n) access to the minimum, so `extractMin` removes and returns `data[0]`. But that leaves a hole at the root — the fix is to move the *last* element into that hole (keeping the tree complete) and then "sift it down," repeatedly swapping with its smaller child until the heap property holds again.

```java
int extractMin() {
    if (isEmpty()) throw new IllegalStateException("Heap is empty");
    int min = data[0];
    size--;
    data[0] = data[size]; // move the last element to the root, keeping the tree complete
    siftDown(0);
    return min;
}

private void siftDown(int i) {
    while (true) {
        int left = 2 * i + 1;
        int right = 2 * i + 2;
        int smallest = i;

        if (left < size && data[left] < data[smallest]) smallest = left;
        if (right < size && data[right] < data[smallest]) smallest = right;

        if (smallest == i) break; // both children are already >= this node -- done
        swap(i, smallest);
        i = smallest; // continue checking from the new position
    }
}
```

`siftDown` here is exactly Day 57's `heapify`, applied to a resizable, standalone structure instead of one shrinking phase of an in-place sort — same swap-with-the-smaller-child logic, same O(log n) bound, same reason (the walk is bounded by the tree's guaranteed-O(log n) height).

## Complexity summary

- **`insert`** — O(log n): append, then sift up at most `height` levels.
- **`extractMin` / `peek` the min** — O(log n) / O(1): the minimum is always at the root, but removing it costs the sift-down.
- **Building a heap from `n` existing elements** — O(n) total (not O(n log n)) via `heapify` from the bottom up, exactly as Day 57's build-heap phase did; most nodes near the bottom of the tree need almost no sifting, which is what keeps the total below the naively-expected O(n log n).

## Max-heap: flip the comparison

A max-heap is the mirror image — every parent is *greater than or equal to* both children, largest at the root — built by flipping every `<`/`<=` comparison in `siftUp`/`siftDown` to `>`/`>=`. This is exactly how Day 62's `new PriorityQueue<>(Comparator.reverseOrder())` produces a max-priority queue from a min-heap-based class: same structure, inverted ordering.
