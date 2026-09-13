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
