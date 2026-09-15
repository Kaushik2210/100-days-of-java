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

## Heap sort's properties

- **O(n log n) in the best, worst, and average case** — like merge sort (Day 55), and unlike quick sort's (Day 56) O(n²) worst case, since a heap's structure never depends on the input's original order the way quick sort's pivot choice does.
- **O(1) extra space** — like Day 54's simple sorts, and unlike merge sort's O(n) auxiliary arrays; heap sort only ever swaps elements within the original array.
- **Not stable** — the swaps during `heapify` and the root-to-end swap during extraction can easily move an element past an equal one, the same instability pattern seen in selection sort (Day 54) and quick sort (Day 56).

Heap sort is the answer whenever a guaranteed worst case *and* O(1) space both matter simultaneously — a combination quick sort and merge sort each only give up one side of.

## The comparison-sort lower bound: why O(n log n) is unbeatable

Every general-purpose sort covered this week — merge, quick, heap — determines the final order using only pairwise comparisons (`is a < b?`). It turns out **no comparison-based sort can do better than O(n log n) in the worst case**, no matter how cleverly it's written. The reasoning: sorting `n` distinct elements has `n!` possible orderings, and each comparison can only distinguish between two outcomes (yes/no) — so a decision tree of comparisons needs at least `log₂(n!)` levels to tell all `n!` orderings apart. By Stirling's approximation, `log₂(n!)` is Θ(n log n), so any comparison sort needs at least that many comparisons in the worst case.

This is why merge sort, quick sort, and heap sort all land on the same O(n log n) worst/average-case shape — they aren't failing to find something faster; O(n log n) is a hard floor for this *class* of algorithm. Beating it requires giving up general comparisons entirely and exploiting extra structure in the data — counting sort and radix sort do exactly this (by using the actual values as array indices rather than comparing them), achieving O(n) time under the right conditions, at the cost of only working for specific kinds of data (small-range integers, fixed-width keys) rather than arbitrary comparable elements.
