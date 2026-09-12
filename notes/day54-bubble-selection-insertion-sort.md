# Day 54: Sorting Algorithms — Bubble, Selection & Insertion Sort

Sorting rearranges a collection into order — and it's foundational precisely because so many other algorithms (binary search from Day 53, among many others) depend on data already being sorted. Today covers the three simplest sorting algorithms: all O(n²) in the worst case, but each with a distinctly different mechanism worth understanding before reaching for the faster O(n log n) algorithms in Days 55–57.

## Bubble sort: repeatedly swap adjacent out-of-order pairs

Bubble sort makes repeated passes over the array, swapping any adjacent pair that's out of order. After each full pass, the largest remaining unsorted element has "bubbled up" to its correct position at the end.

```java
void bubbleSort(int[] arr) {
    for (int i = 0; i < arr.length - 1; i++) {
        boolean swapped = false;
        for (int j = 0; j < arr.length - 1 - i; j++) { // shrinks each pass -- the tail is already sorted
            if (arr[j] > arr[j + 1]) {
                int temp = arr[j];
                arr[j] = arr[j + 1];
                arr[j + 1] = temp;
                swapped = true;
            }
        }
        if (!swapped) break; // no swaps this pass means the array is already sorted -- stop early
    }
}
```

The early-exit `swapped` flag makes bubble sort O(n) best case (already-sorted input finishes in one pass) even though it's O(n²) worst case (reverse-sorted input) — the same best/worst-case distinction from Day 51.

## Selection sort: repeatedly pick the minimum

Selection sort divides the array into a sorted prefix and an unsorted suffix. Each pass finds the minimum element in the unsorted part and swaps it into place at the front of that part — unlike bubble sort, which swaps adjacent elements constantly, selection sort does at most one swap per pass.

```java
void selectionSort(int[] arr) {
    for (int i = 0; i < arr.length - 1; i++) {
        int minIndex = i;
        for (int j = i + 1; j < arr.length; j++) {
            if (arr[j] < arr[minIndex]) {
                minIndex = j; // track the smallest found so far in the unsorted part
            }
        }
        int temp = arr[minIndex]; // one swap per outer-loop pass, regardless of input order
        arr[minIndex] = arr[i];
        arr[i] = temp;
    }
}
```

Selection sort is always O(n²), even on already-sorted input — it always scans the full unsorted remainder to find the minimum, so there's no early-exit opportunity like bubble sort's.
