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

## Variable-size window: growing and shrinking as needed

Not every problem has a fixed window size — sometimes the window itself needs to grow or shrink depending on what it currently contains. A classic example: find the length of the **longest substring with no repeated characters**. The window expands by moving `right` forward, absorbing new characters; when a repeat is found, the window shrinks from the left until the repeat is gone.

```java
int longestUniqueSubstring(String s) {
    Set<Character> window = new HashSet<>(); // Day 24's HashSet -- tracks what's currently inside the window
    int left = 0;
    int maxLength = 0;

    for (int right = 0; right < s.length(); right++) {
        char c = s.charAt(right);
        while (window.contains(c)) {          // duplicate found -- shrink from the left until it's gone
            window.remove(s.charAt(left));
            left++;
        }
        window.add(c);
        maxLength = Math.max(maxLength, right - left + 1); // current window size
    }
    return maxLength;
}
```

Even though there's a `while` loop nested inside the `for` loop, this is still O(n) overall, not O(n²): `left` only ever moves forward, and across the entire run it can advance at most `n` times total — the same "each pointer only ever moves one direction, bounding the total work" argument Day 71 used for two pointers, now applied to a window's two edges instead of two independent pointers.

## Recognizing when sliding window applies

The pattern fits whenever a problem asks about **contiguous** ranges (a subarray or substring, not an arbitrary subset) and the answer for one window can be derived cheaply from the answer for the adjacent window — an O(1) update on slide, rather than needing to reprocess the whole range. Non-contiguous problems (any subset, in any order) generally need a different approach entirely — often the dynamic programming techniques covered starting Day 76.
