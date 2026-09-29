# Day 71: Two Pointers Technique

Phase 9 shifts focus from specific data structures to **patterns** — reusable shapes of solution that show up across many different problems. The two pointers technique replaces a nested-loop O(n²) scan with two indices moving through the data in a coordinated way, often getting the same answer in O(n).

## Opposite-ends pointers: converging from both sides

A classic problem: given a **sorted** array, find two numbers that add up to a target. The brute-force approach checks every pair — O(n²) (Day 51). With the array sorted, two pointers starting at each end can solve it in one O(n) pass: if the current pair's sum is too small, the only way to increase it is to move the left pointer right (a bigger left value); if it's too large, move the right pointer left.

```java
int[] twoSumSorted(int[] sortedArr, int target) {
    int left = 0;
    int right = sortedArr.length - 1;

    while (left < right) {
        int sum = sortedArr[left] + sortedArr[right];
        if (sum == target) {
            return new int[]{left, right};
        } else if (sum < target) {
            left++;  // sum too small -- only a bigger left value can help, and the array is sorted
        } else {
            right--; // sum too large -- only a smaller right value can help
        }
    }
    return new int[]{-1, -1}; // no pair found
}
```

Each step moves `left` right or `right` left — never both stay put, never either reverses — so the two pointers can cross at most `n` times total, making this O(n) instead of the brute-force O(n²): a direct application of Day 53's "sorted data lets you eliminate possibilities without checking them" idea, just applied to pairs instead of a single search target.

This only works *because* the array is sorted — the reasoning "moving `left` right can only increase the sum" depends entirely on that ordering. On unsorted data, the two-pointer approach doesn't apply directly (though sorting first, Day 55/56, and then running it is often still faster than the brute-force O(n²) scan).
