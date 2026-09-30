# Day 72: Sliding Window Technique

Sliding window is a close cousin of Day 71's two pointers — two indices bound a contiguous range ("the window") over an array or string, and the window slides forward, reusing work from the previous position instead of recomputing everything from scratch. It's the standard fix for "look at every contiguous subarray/substring" problems that would otherwise cost O(n²) or worse.

## Fixed-size window: maximum sum of any k consecutive elements

Brute force checks every window of size `k` independently, summing each from scratch — O(n·k). A sliding window instead computes the first window's sum once, then updates it incrementally: subtract the element leaving the window, add the element entering it.

```java
int maxSumFixedWindow(int[] arr, int k) {
    int windowSum = 0;
    for (int i = 0; i < k; i++) {
        windowSum += arr[i]; // sum of the very first window, computed once
    }

    int maxSum = windowSum;
    for (int i = k; i < arr.length; i++) {
        windowSum += arr[i] - arr[i - k]; // slide forward: add the new element, drop the oldest one
        maxSum = Math.max(maxSum, windowSum);
    }
    return maxSum;
}
```

Each slide is O(1) — one addition, one subtraction — instead of re-summing all `k` elements, bringing the whole scan down to O(n) regardless of `k`. This is the same "reuse the previous result instead of recomputing" idea Day 51's amortized analysis and Day 57's O(n) build-heap both rely on, just applied to a running sum.
