# Day 94: Problem-Solving Patterns — Arrays & Strings

Most array and string problems reduce to a handful of reusable patterns. Days 71 and 72 already covered **two pointers** and **sliding window**, so today covers six others, each with a one-line trigger for when to reach for it. Every pattern in [`ArrayStringPatternsDemo.java`](../src/day94/ArrayStringPatternsDemo.java) is checked against a brute-force version on 2,000 random cases (fixed seed 94), and all of them matched.

| Pattern | Reach for it when | Time | Extra space |
|---------|-------------------|------|-------------|
| Prefix sums + hash map | counting or finding subarrays with a target sum, negatives allowed | O(n) | O(n) |
| Kadane's scan | best contiguous subarray | O(n) | O(1) |
| In-place partition | grouping a few distinct values in one pass | O(n) | O(1) |
| Monotonic stack | "next greater / smaller element" | O(n) | O(n) |
| Frequency counting | anagrams, character-set questions | O(n) | O(alphabet) |
| Difference array | many range updates, one read at the end | O(n + q) | O(n) |

## 1. Prefix sums + hash map

A sliding window needs non-negative numbers, because only then does extending the window always grow the sum. With negatives, use prefix sums. Let `prefix[j]` be the sum of the first `j` elements. The subarray `(i, j]` sums to `k` exactly when `prefix[j] - prefix[i] == k`. While scanning, count how many earlier prefixes equal `prefix - k`.

```java
seen.put(0, 1); // the empty prefix, so subarrays starting at index 0 are counted
for (int x : a) {
    prefix += x;
    count += seen.getOrDefault(prefix - k, 0);
    seen.merge(prefix, 1, Integer::sum);
}
```

Seeding the map with `0 -> 1` is the usual bug site. Without it, subarrays that start at index 0 are missed. On `[1, -1, 0]` with `k = 0` the answer is 3 (`[1, -1]`, `[0]` and `[1, -1, 0]`), which only comes out right with the seed.

On 20,000 random values the map version took 3.7 ms against 67.5 ms for the O(n²) loop, and both found the same 166,584 subarrays.

## 2. Kadane's scan

At each element, either extend the best run ending at the previous element or start fresh here: `run = max(a[i], run + a[i])`. Track the best `run` seen. Start `best` and `run` from `a[0]`, not `0`, so an all-negative array returns its largest element (`[-3, -1, -2]` gives `-1`) instead of an empty sum of `0`.

## 3. In-place partition (Dutch national flag)

Three pointers carve the array into regions: `[0, low)` are 0s, `[low, mid)` are 1s, `(high, end]` are 2s, and `[mid, high]` is unexamined.

```java
if (a[mid] == 0) swap(a, low++, mid++);
else if (a[mid] == 2) swap(a, mid, high--); // do not advance mid
else mid++;
```

Swapping a 2 to the back does **not** advance `mid`, because the value swapped in has not been looked at yet. Swapping a 0 forward does advance it, because what comes back from `low` is known to be a 1. One pass, no counting array, and the same idea underlies quicksort's partition step.

## 4. Monotonic stack

For "how far to the next greater element", keep a stack of indexes whose values are non-increasing. When a new value beats the one on top, that top has found its answer, so pop it and record the distance. Each index is pushed once and popped at most once, so the loop is O(n) even though it contains a nested `while`.

Check on `[73, 74, 75, 71, 69, 72, 76, 73]`: the result is `[1, 1, 4, 2, 1, 1, 0, 0]`. Indexes still on the stack at the end never found a greater value and keep `0`.

This is the same amortized argument as day 93: the inner loop is bounded by the total number of pushes.

## 5. Frequency counting

For lowercase letters, a `int[26]` beats a `HashMap<Character, Integer>` for both speed and memory. For anagrams, add one for each letter of `s` and subtract one for each letter of `t`. They are anagrams exactly when every count ends at zero. This is O(n), versus O(n log n) for sorting both strings, which is what the brute-force check in the demo does.

If the alphabet is not fixed (say, any Unicode), switch to a map. The array version is only valid because `c - 'a'` is a safe index.

## 6. Difference array

To add `v` to every cell in `[l, r]`, do `diff[l] += v` and `diff[r + 1] -= v`. After all updates, a single running sum rebuilds the real array. That makes each update O(1) rather than O(range), so `q` updates over `n` cells cost O(n + q) instead of O(n·q).

Check: on 5 cells, `+2` over `[1, 3]` and `+3` over `[2, 4]` gives `[0, 2, 5, 5, 3]`. The array needs `n + 1` slots so that `r + 1` is a valid index when `r = n - 1`.

It is the inverse of the prefix sum: prefix sums answer range *queries* cheaply, and difference arrays make range *updates* cheap.

## Choosing a pattern

Ask these questions in order:

1. Does the answer depend on a contiguous range? Try prefix sums or a window (days 71–72).
2. Are the values all non-negative? Then a window is likely enough. If not, prefix sums plus a map.
3. Is it "nearest larger/smaller to the side"? Monotonic stack.
4. Is the value set tiny (3 colors, 26 letters)? Counting or partitioning, no sort needed.
5. Are there many range edits and a single read? Difference array.

## Takeaways

- Write the brute force first. It states the problem precisely and doubles as the test oracle for the fast version.
- Most of these bugs are about boundaries: the empty prefix, the all-negative array, the `r + 1` slot, and whether a swap advances a pointer.
- A nested loop is not automatically quadratic. The monotonic stack's inner `while` is paid for by the pushes.
