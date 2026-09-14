# Day 56: Sorting Algorithms — Quick Sort

Quick sort is another divide-and-conquer sort (Day 55), but instead of always splitting in half and doing the real work in the *merge* step, it does the real work in the *split* step: pick a **pivot**, rearrange the array so everything smaller than the pivot ends up to its left and everything larger ends up to its right, then recursively sort each side. Once that's done, the whole array is sorted — there's no merge step at all.

## Partitioning: the core operation

Partitioning picks a pivot (the last element, in this common scheme) and rearranges the sub-array so the pivot lands in its final sorted position, with everything smaller before it and everything larger after.

```java
int partition(int[] arr, int low, int high) {
    int pivot = arr[high]; // choosing the last element as the pivot
    int i = low - 1;       // boundary of "elements confirmed smaller than pivot"

    for (int j = low; j < high; j++) {
        if (arr[j] < pivot) {
            i++;
            swap(arr, i, j); // grow the "smaller than pivot" region by one
        }
    }
    swap(arr, i + 1, high); // place the pivot right after its smaller elements -- now in its final position
    return i + 1;           // the pivot's final, sorted index
}

void swap(int[] arr, int a, int b) {
    int temp = arr[a];
    arr[a] = arr[b];
    arr[b] = temp;
}
```

## Quick sort: partition, then recurse on each side

```java
void quickSort(int[] arr, int low, int high) {
    if (low >= high) return; // base case -- zero or one element is already sorted

    int pivotIndex = partition(arr, low, high);
    quickSort(arr, low, pivotIndex - 1);  // everything left of the pivot
    quickSort(arr, pivotIndex + 1, high); // everything right of the pivot
}
```

Unlike merge sort, quick sort rearranges elements **in place** within the original array — `partition` only ever swaps elements, allocating no auxiliary array — which is one of its main practical advantages.

## Average case O(n log n), worst case O(n²)

When the pivot splits the array roughly in half each time, quick sort recurses `log n` levels deep, doing O(n) partitioning work per level — the same O(n log n) shape as merge sort, and in practice quick sort is often *faster* than merge sort despite matching Big-O, thanks to better cache locality (Day 43) from working in place rather than allocating new arrays.

But if the pivot is consistently the **smallest or largest** remaining element — which happens on an already-sorted (or reverse-sorted) array when always picking the last element as pivot — each partition only shaves off one element instead of splitting the array in half. That degrades to `n` levels of recursion doing O(n) work each, giving O(n²): quick sort's worst case, and precisely the input pattern this scheme is most vulnerable to.

## Pivot selection strategies

Since the worst case is triggered by a *specific, predictable* input pattern (already-sorted data), production implementations avoid always picking a fixed position:

- **Random pivot** — pick a uniformly random element as the pivot each time. Makes the worst case exceedingly unlikely for any specific input, since an adversary would need to guess the random choices to trigger it.
- **Median-of-three** — pick the median of the first, middle, and last elements. Cheap to compute, and reliably avoids the worst case on already- or nearly-sorted data specifically.

Either strategy keeps quick sort's expected behavior at O(n log n) for essentially any real-world input, which is why quick sort (with one of these safeguards) remains a common default despite its theoretical O(n²) worst case.

## Space complexity and stability

Quick sort uses O(log n) extra space in the average case — not the O(1) of Day 54's sorts, since recursion itself needs stack frames (Day 42), but far less than merge sort's O(n) auxiliary arrays. Quick sort is generally **not stable** — the partition step can swap two equal elements past each other, the same instability issue Day 54 identified in selection sort.

## Choosing between merge sort and quick sort

- **Quick sort** — faster in practice for in-memory arrays (better cache locality, in-place, less overhead), the common default in JDK's primitive-array `Arrays.sort` (int[], double[], etc.).
- **Merge sort** — guaranteed O(n log n) with no bad-input risk, and stable, so it's the better choice for object arrays (where the JDK's `Arrays.sort`/`Collections.sort` use a merge-sort variant, Timsort) and any situation where worst-case guarantees or stability genuinely matter.
