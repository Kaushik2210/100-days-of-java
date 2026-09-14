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
