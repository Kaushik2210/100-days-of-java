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
