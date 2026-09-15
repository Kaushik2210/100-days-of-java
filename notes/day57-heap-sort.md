# Day 57: Sorting Algorithms — Heap Sort & the Comparison-Sort Lower Bound

Heap sort gets Day 55's guaranteed O(n log n) *and* Day 56's O(1) extra space (true in-place sorting) — the combination neither pure merge sort nor pure quick sort offers on its own. It does this by building a **binary heap** (Day 66 covers heaps as a standalone data structure in depth) directly inside the array being sorted.

## A binary heap, represented as an array

A **max-heap** is a binary tree where every parent is greater than or equal to both its children — the largest element is always at the root. Stored as a plain array with no pointers at all, node `i`'s children live at indices `2i + 1` and `2i + 2`, and its parent lives at `(i - 1) / 2` — pure arithmetic, no linked structure needed.

```java
void heapify(int[] arr, int heapSize, int i) {
    int largest = i;
    int left = 2 * i + 1;
    int right = 2 * i + 2;

    if (left < heapSize && arr[left] > arr[largest]) largest = left;
    if (right < heapSize && arr[right] > arr[largest]) largest = right;

    if (largest != i) {
        int temp = arr[i];        // sink arr[i] down by swapping with its larger child
        arr[i] = arr[largest];
        arr[largest] = temp;
        heapify(arr, heapSize, largest); // continue sinking from the new position
    }
}
```

`heapify` assumes both subtrees rooted at `i`'s children are already valid heaps, and fixes just the root if it's out of place — sinking it down until the max-heap property holds again.

## Heap sort: build a heap, then repeatedly extract the max

```java
void heapSort(int[] arr) {
    int n = arr.length;

    // build-heap: heapify every non-leaf node, from the bottom up
    for (int i = n / 2 - 1; i >= 0; i--) {
        heapify(arr, n, i);
    }

    // repeatedly move the current max (the root) to the end, then re-heapify the shrunken heap
    for (int end = n - 1; end > 0; end--) {
        int temp = arr[0];   // the root is always the largest remaining element
        arr[0] = arr[end];
        arr[end] = temp;
        heapify(arr, end, 0); // heap shrinks by one each time -- the sorted tail grows from the back
    }
}
```

After building the heap once (O(n) total, a known tighter bound than the naive O(n log n) estimate), each of the `n` extractions does an O(log n) `heapify` — giving O(n log n) overall, in every case, with zero extra arrays.
