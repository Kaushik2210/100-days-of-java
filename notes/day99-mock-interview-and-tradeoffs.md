# Day 99: Mock Coding Interview Practice & Complexity Tradeoffs

Days 51 to 97 built a toolbox. A coding interview tests something slightly different: can you pick the right tool *under time pressure, while explaining your thinking*, and can you say honestly what each choice costs? Today is a rehearsal. It sets out a repeatable process, then walks three classic problems from a slow-but-correct first answer to an optimized one, measuring the trade-off each time.

## A process that works for any problem

1. **Restate the problem and ask questions.** Can the input be empty? Can values repeat or be negative? How large can `n` be? Is the array sorted? (The size of `n` tells you which complexity you are aiming for: `n` around 10^5 means you need roughly O(n log n) or better.)
2. **Work a small example by hand**, including an edge case. This is also your first test case.
3. **State a brute-force solution and its complexity out loud**, even if you won't code it. It proves you understand the problem and gives you a baseline.
4. **Find the bottleneck and name the technique** that removes it: a hash map for lookups, sorting for ordering, a heap for "top k", two pointers for sorted data.
5. **Code the optimized version**, using clear names.
6. **Test it** with your example, then the edge cases (empty, one element, duplicates, all equal).
7. **State the final time and space complexity**, and what you traded to get it.

Step 3 is not wasted time. Interviewers often care more about whether you can *reason* from correct-but-slow to fast than whether you recall the fast answer instantly. It is also the habit used throughout these notes: write a trusted brute-force version and check the clever one against it.

## Problem 1: Two Sum

*Given an array of integers and a target, return the indices of two different elements that add up to the target.*

**Brute force.** Try every pair. `O(n²)` time, `O(1)` space.

```java
int[] twoSumBrute(int[] nums, int target) {
    for (int i = 0; i < nums.length; i++)
        for (int j = i + 1; j < nums.length; j++)
            if (nums[i] + nums[j] == target) return new int[]{i, j};
    return null;
}
```

**Bottleneck:** for each element we search the rest of the array for its partner. A search can be replaced by a hash lookup (Day 67).

**Hash map.** Walk once; for each number, ask whether its *complement* (`target - x`) has already been seen.

```java
int[] twoSumHash(int[] nums, int target) {
    Map<Integer, Integer> seen = new HashMap<>();        // value -> index
    for (int i = 0; i < nums.length; i++) {
        Integer partner = seen.get(target - nums[i]);
        if (partner != null) return new int[]{partner, i};
        seen.put(nums[i], i);                            // add AFTER checking, so an element can't pair with itself
    }
    return null;
}
```

`O(n)` time, `O(n)` space. Checking *before* inserting is the subtle detail: it prevents `target = 6` with `nums = [3]` from "finding" 3 + 3 using the same element, while still allowing `[3, 3]` to match two different elements.

**The trade-off.** The hash map buys time with memory. If memory is tight, or the array is already sorted, Day 71's two pointers do it in `O(n)` time and `O(1)` extra space, but only on sorted data (sorting first costs `O(n log n)`, and indices must then be tracked separately). There is no universally best answer, which is exactly what an interviewer wants you to say.

**Edge cases to test:** a negative number, a zero target, duplicates that form the answer, and no valid pair.

## Problem 2: Top K Frequent Elements

*Given an array and an integer `k`, return the `k` values that occur most often.*

Every approach starts the same way: count occurrences with a `HashMap` (Day 25), `O(n)`. The question is how to pick the top `k` from the `d` distinct values. There are three reasonable answers, with three different costs, which is why this problem is so popular.

**Approach A: sort the entries by count.** `O(d log d)`.

```java
List<Integer> topKSort(int[] nums, int k) {
    Map<Integer, Integer> counts = new HashMap<>();
    for (int x : nums) counts.merge(x, 1, Integer::sum);
    List<Integer> values = new ArrayList<>(counts.keySet());
    values.sort((a, b) -> counts.get(b) - counts.get(a));     // most frequent first
    return values.subList(0, Math.min(k, values.size()));
}
```

Simple and usually fast enough. It does more work than needed, since it fully orders values we will discard.

**Approach B: a min-heap of size k.** `O(d log k)`. Keep the best `k` seen so far in a min-heap (Day 66); when it grows past `k`, evict the *smallest* count. When `k` is much smaller than `d`, each heap operation is cheap.

```java
List<Integer> topKHeap(int[] nums, int k) {
    Map<Integer, Integer> counts = new HashMap<>();
    for (int x : nums) counts.merge(x, 1, Integer::sum);
    PriorityQueue<Integer> heap = new PriorityQueue<>((a, b) -> counts.get(a) - counts.get(b)); // smallest count on top
    for (int value : counts.keySet()) {
        heap.add(value);
        if (heap.size() > k) heap.poll();                       // drop the least frequent of the k+1
    }
    return new ArrayList<>(heap);
}
```

**Approach C: bucket by frequency.** `O(n)`. A value can occur at most `n` times, so make an array of `n + 1` buckets where bucket `f` holds the values that occur exactly `f` times, then read buckets from the highest frequency downward until `k` values are collected. This swaps the comparison-based `log` factor (recall Day 57's lower bound) for extra memory, using the fact that the keys being sorted are small integers.

```java
List<Integer> topKBucket(int[] nums, int k) {
    Map<Integer, Integer> counts = new HashMap<>();
    for (int x : nums) counts.merge(x, 1, Integer::sum);
    List<List<Integer>> buckets = new ArrayList<>();
    for (int i = 0; i <= nums.length; i++) buckets.add(new ArrayList<>());
    for (Map.Entry<Integer, Integer> e : counts.entrySet()) buckets.get(e.getValue()).add(e.getKey());

    List<Integer> result = new ArrayList<>();
    for (int f = nums.length; f >= 1 && result.size() < k; f--) {
        for (int value : buckets.get(f)) {
            if (result.size() == k) break;                       // a tie at the boundary may have more values than we need
            result.add(value);
        }
    }
    return result;
}
```

**The trade-off in one table.**

| Approach | Time | Extra space | Best when |
|---|---|---|---|
| Sort | O(d log d) | O(d) | simplicity matters, `d` is small |
| Min-heap of size k | O(d log k) | O(d + k) | `k` is much smaller than `d` |
| Frequency buckets | O(n) | O(n) | you need guaranteed linear time |

**Big-O is not the whole story.** When the demo times all three on 2,000,000 elements with 50,000 distinct values and `k = 10`, the O(n) bucket version is the *slowest*, roughly three to four times slower than the other two (for example about 150 ms against 45 ms in one run; exact times vary from run to run). It allocates `n + 1` lists, two million of them, to serve only 50,000 distinct values, and with a small `k` the heap's `log k` factor is nearly free. Asymptotics describe how cost *grows*; constant factors, memory allocation, and the shape of the actual input decide which approach wins at a given size. In an interview, say both: "bucketing is linear in theory, but it carries real allocation overhead, so for small `k` I would reach for the heap."

**Correctness subtlety to mention aloud:** when several values tie at the boundary (say `k = 2` but three values each occur 4 times), more than one answer is valid. A good test therefore checks the *frequencies* of the returned values rather than demanding one exact list.

## Problem 3: Merge Overlapping Intervals

*Given a list of closed intervals `[start, end]`, merge all that overlap.* (Intervals that merely touch, like `[1, 3]` and `[3, 5]`, do overlap at the point 3.)

**Brute force** repeatedly searches for any two overlapping intervals and merges them until none remain, which is `O(n²)` or worse and awkward to write correctly.

**The key idea is to sort by start.** After sorting, any interval that could overlap the current merged one must come *next*; once an interval starts beyond the current end, nothing later can reach back. A single left-to-right sweep suffices.

```java
int[][] mergeIntervals(int[][] intervals) {
    int[][] sorted = intervals.clone();                          // do not reorder the caller's data
    Arrays.sort(sorted, (a, b) -> Integer.compare(a[0], b[0]));
    List<int[]> merged = new ArrayList<>();
    for (int[] current : sorted) {
        if (merged.isEmpty() || merged.get(merged.size() - 1)[1] < current[0]) {
            merged.add(new int[]{current[0], current[1]});       // a gap: start a new merged interval
        } else {
            int[] last = merged.get(merged.size() - 1);
            last[1] = Math.max(last[1], current[1]);             // overlap: extend (it might be fully contained, so take the max)
        }
    }
    return merged.toArray(new int[0][]);
}
```

`O(n log n)` time, dominated by the sort, and `O(n)` space. The `Math.max` is the classic bug to avoid: writing `last[1] = current[1]` shrinks the interval when `current` is fully inside it (`[1, 10]` then `[2, 3]`).

This is the Day 74 greedy pattern again: sort, then make one irrevocable decision per element.

## What to say at the end

A strong close names the complexity, the trade-off, and what you would do differently under different constraints: "This is O(n log n) because of the sort. If the intervals arrived already sorted it would be O(n). If memory were the constraint I would..." That one sentence shows you understand your own solution rather than having memorized it.
