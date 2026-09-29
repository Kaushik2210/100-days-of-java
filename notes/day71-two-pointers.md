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

## Same-direction pointers: a fast one and a slow one

The other two-pointer shape moves both pointers the *same* way, at different speeds — a **slow** pointer marking where the next "good" element should go, and a **fast** pointer scanning ahead to find one.

A common use: removing duplicates from a sorted array **in place**, using only O(1) extra space (no new array, unlike a `Set`-based dedup, Day 24).

```java
int removeDuplicates(int[] sortedArr) {
    if (sortedArr.length == 0) return 0;

    int slow = 0; // index of the last confirmed-unique element
    for (int fast = 1; fast < sortedArr.length; fast++) {
        if (sortedArr[fast] != sortedArr[slow]) {
            slow++;
            sortedArr[slow] = sortedArr[fast]; // write the new unique value right after the last one
        }
        // if equal, fast just keeps scanning -- slow doesn't move, so the duplicate gets overwritten later
    }
    return slow + 1; // number of unique elements, now sitting in sortedArr[0..slow]
}
```

`fast` scans every element once; `slow` only advances (and writes) when a genuinely new value is found — so the array's unique elements end up compacted into its front, in O(n) time and O(1) extra space.

## Fast/slow pointers: detecting a cycle (Floyd's algorithm)

The same fast/slow idea, applied to a linked list (Day 58) instead of an array, detects a cycle without any extra memory: advance `slow` one node at a time and `fast` two nodes at a time. If the list has a cycle, `fast` will eventually lap `slow` and they'll meet inside the loop; if it doesn't, `fast` simply reaches the end (`null`) first.

```java
boolean hasCycle(Node head) {
    Node slow = head;
    Node fast = head;

    while (fast != null && fast.next != null) {
        slow = slow.next;       // one step
        fast = fast.next.next;  // two steps
        if (slow == fast) return true; // fast caught up to slow -- must be a cycle
    }
    return false; // fast reached the end -- no cycle
}
```

This is often called the "tortoise and hare" algorithm — it's O(n) time and O(1) space, compared to the O(n) *space* a `HashSet`-based "have I visited this node before?" check (Day 67) would need instead.
