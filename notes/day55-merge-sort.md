# Day 55: Sorting Algorithms — Merge Sort

Day 54's three sorts are all O(n²) because each one repeatedly re-scans large portions of the array. Merge sort breaks that pattern with **divide and conquer** (Day 51's O(n log n) class, Day 73 covers the general strategy in depth): split the problem in half, solve each half recursively, then combine the two sorted halves cheaply.

## The idea: split, recurse, merge

Merge sort has two phases:

1. **Divide** — split the array into two halves, recursively sort each half (down to the base case of a single element, which is trivially sorted).
2. **Merge** — combine the two now-sorted halves into one sorted whole by repeatedly taking the smaller of the two halves' front elements.

```java
void mergeSort(int[] arr, int left, int right) {
    if (left >= right) return; // base case -- a single element (or empty range) is already sorted

    int mid = left + (right - left) / 2;
    mergeSort(arr, left, mid);       // sort the left half
    mergeSort(arr, mid + 1, right);  // sort the right half
    merge(arr, left, mid, right);    // combine the two sorted halves
}
```

Because the array is split roughly in half at every level (like Day 53's binary search) and every element gets touched once per level during the merge, the total work is O(n) per level times O(log n) levels — giving O(n log n) overall, dramatically better than Day 54's O(n²) for large inputs.

## The merge step

Merging two already-sorted sub-arrays into one sorted array only needs a single pass, comparing the front of each sub-array and taking the smaller each time.

```java
void merge(int[] arr, int left, int mid, int right) {
    int[] leftPart = Arrays.copyOfRange(arr, left, mid + 1);
    int[] rightPart = Arrays.copyOfRange(arr, mid + 1, right + 1);

    int i = 0, j = 0, k = left;
    while (i < leftPart.length && j < rightPart.length) {
        if (leftPart[i] <= rightPart[j]) { // <= (not <) keeps equal elements in original relative order -- stable
            arr[k++] = leftPart[i++];
        } else {
            arr[k++] = rightPart[j++];
        }
    }
    while (i < leftPart.length) arr[k++] = leftPart[i++]; // copy any remaining elements from whichever side is longer
    while (j < rightPart.length) arr[k++] = rightPart[j++];
}
```

Using `<=` rather than `<` when the two front elements are equal is what makes merge sort **stable** (Day 54) — the element from the left (earlier) half is always taken first when there's a tie.

## Space complexity: the cost of merge sort's speed

Unlike Day 54's three sorts, which rearrange elements within the original array using O(1) extra space, merge sort's `merge` step allocates temporary arrays (`leftPart`, `rightPart`) to hold copies of each half before writing the merged result back — O(n) extra space at each level of recursion (though only O(n) total across the whole sort, since the temporary arrays at any one time don't all exist simultaneously). This is merge sort's real tradeoff: guaranteed O(n log n) time, in exchange for giving up the O(1)-space, in-place property that bubble/selection/insertion sort have.

## Merge sort's consistency

Merge sort is O(n log n) in the **best, worst, and average case alike** — unlike Day 54's sorts, its performance doesn't depend on how sorted the input already is, because it always fully divides and always fully merges regardless of the data's order. That consistency, combined with stability, is why merge sort (or a close relative of it) is used inside many standard-library sorts for objects — including `Collections.sort` and `Arrays.sort` for reference types in the JDK, which use a variant called Timsort that borrows merge sort's merging step.

## Comparing against Day 54's sorts

On a large, randomly-ordered array, merge sort's O(n log n) should noticeably outperform Day 54's O(n²) insertion sort — the same kind of measured comparison Day 51 used for O(n) vs O(n²), now applied to two real, general-purpose sorting algorithms.
