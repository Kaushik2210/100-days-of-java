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
