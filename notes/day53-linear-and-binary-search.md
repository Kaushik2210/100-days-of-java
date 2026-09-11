# Day 53: Searching Algorithms — Linear & Binary Search

Searching for a value in a collection is one of the most common operations in any program, and how fast it can be done depends entirely on what assumptions can be made about the data — sorted or not.

## Linear search: O(n), works on anything

Linear search checks every element in order until it finds a match or runs out of elements. It makes no assumptions about the data at all — it works on an unsorted array just as well as a sorted one — which is exactly why it can't do better than O(n) (Day 51): with no structure to exploit, there's no way to rule out any element without actually looking at it.

```java
int linearSearch(int[] arr, int target) {
    for (int i = 0; i < arr.length; i++) {
        if (arr[i] == target) return i; // found it -- return the index
    }
    return -1; // not found
}
```

Best case O(1) (the target is first), worst case O(n) (the target is last, or absent) — matching Day 51's best/worst-case distinction exactly.

## Binary search: O(log n), but requires sorted data

If the array is sorted, there's a much faster approach: compare the target to the middle element. If it matches, done. If the target is smaller, the entire right half can be discarded without looking at it — every value there is guaranteed larger. If it's larger, discard the left half the same way. Each comparison eliminates half the remaining search space, giving O(log n) — the same shape of growth as Day 51's logarithmic class.

```java
int binarySearch(int[] sortedArr, int target) {
    int low = 0;
    int high = sortedArr.length - 1;

    while (low <= high) {
        int mid = low + (high - low) / 2; // avoids overflow vs (low + high) / 2 for very large arrays
        if (sortedArr[mid] == target) {
            return mid;
        } else if (sortedArr[mid] < target) {
            low = mid + 1;  // target must be in the right half
        } else {
            high = mid - 1; // target must be in the left half
        }
    }
    return -1; // not found
}
```

Binary search on a sorted array of a billion elements takes at most ~30 comparisons; linear search could take a billion. The tradeoff: binary search only works if the data is already sorted (or the cost of sorting it, Day 55/56, is paid once up front and amortized across many searches).
