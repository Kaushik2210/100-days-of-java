# Day 73: Divide and Conquer Strategy

Days 53, 55, and 56 each already used this strategy without naming it: binary search, merge sort, and quick sort are all **divide and conquer** algorithms. Today names the pattern explicitly and applies it to a problem none of those three cover.

## The three steps

Every divide-and-conquer algorithm follows the same shape:

1. **Divide** — split the problem into smaller subproblems of the same kind.
2. **Conquer** — solve each subproblem, usually by recursing (Day 52) until hitting a trivially small base case.
3. **Combine** — merge the subproblems' solutions into the solution for the original problem.

```
Binary search  (Day 53): divide = pick the middle; conquer = search one half; combine = (nothing — the answer IS the recursive result)
Merge sort     (Day 55): divide = split in half;    conquer = sort each half;    combine = merge the two sorted halves
Quick sort     (Day 56): divide = partition around a pivot; conquer = sort each side; combine = (nothing — already in place)
```

Binary search and quick sort's "combine" step is trivial (there's nothing left to do), which is why they're sometimes described as "decrease and conquer" instead — but they share the identical divide/recurse structure, just with less work at the end.

## A fresh example: fast exponentiation

Naively computing `x^n` takes `n` multiplications — `x * x * x * ... * x`, O(n). Divide and conquer does it in O(log n) instead, using the identity `x^n = (x^(n/2))^2` for even `n` (and one extra factor of `x` for odd `n`): computing `x^(n/2)` once and squaring it does the same work as two separate recursive calls would, at half the recursion depth each time it halves.

```java
long fastPower(long base, int exponent) {
    if (exponent == 0) return 1; // base case

    long half = fastPower(base, exponent / 2); // divide: solve one half-sized subproblem
    long result = half * half;                  // combine: square it

    if (exponent % 2 != 0) {
        result *= base; // odd exponent -- one leftover factor of base
    }
    return result;
}
```

`fastPower(2, 10)` makes only 5 calls total (exponents 10 → 5 → 2 → 1 → 0, including the base case) instead of 10 sequential multiplications — the same halving-the-problem-size idea as binary search, applied to exponentiation instead of a sorted array.

## Maximum subarray: when combine is the hard part

Given an array that can contain negative numbers, find the contiguous subarray with the largest sum. Unlike `fastPower`, where combining two halves' answers was trivial (one multiplication), this problem's **combine** step genuinely has work to do — the best answer might not sit entirely inside the left half or entirely inside the right half, it might straddle the boundary between them.

```java
int maxSubarraySum(int[] arr, int left, int right) {
    if (left == right) return arr[left]; // base case: a single element

    int mid = left + (right - left) / 2;
    int leftMax = maxSubarraySum(arr, left, mid);       // best sum entirely within the left half
    int rightMax = maxSubarraySum(arr, mid + 1, right); // best sum entirely within the right half
    int crossMax = maxCrossingSum(arr, left, mid, right); // best sum that crosses the midpoint

    return Math.max(Math.max(leftMax, rightMax), crossMax); // combine: take the best of all three
}

int maxCrossingSum(int[] arr, int left, int mid, int right) {
    int leftSum = Integer.MIN_VALUE;
    int sum = 0;
    for (int i = mid; i >= left; i--) {       // extend left from the midpoint, tracking the best stopping point
        sum += arr[i];
        leftSum = Math.max(leftSum, sum);
    }

    int rightSum = Integer.MIN_VALUE;
    sum = 0;
    for (int i = mid + 1; i <= right; i++) {  // extend right from the midpoint, tracking the best stopping point
        sum += arr[i];
        rightSum = Math.max(rightSum, sum);
    }

    return leftSum + rightSum; // the best crossing subarray must include both midpoint-adjacent elements
}
```

Each level of recursion does O(n) work in `maxCrossingSum` (scanning outward from the midpoint in both directions), and there are O(log n) levels (the array keeps halving) — giving O(n log n) overall, the exact same shape as merge sort for the exact same reason: O(n) combine work at O(log n) levels. (Day 94 covers an O(n) solution to this same problem — Kadane's algorithm — using a completely different, non-divide-and-conquer approach, a good illustration that divide and conquer is a powerful default but not always the asymptotically optimal one.)
