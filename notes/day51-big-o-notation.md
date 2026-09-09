# Day 51: Big-O Notation & Algorithm Complexity Analysis

Day 50 measured two implementations against each other with a stopwatch. Big-O notation is the *theoretical* tool for the same question — how does an algorithm's cost grow as the input grows — without needing to run it at all. It describes an upper bound on growth rate, ignoring constant factors and lower-order terms, so two algorithms with wildly different actual speeds can still share the same Big-O if they scale the same way.

## What Big-O actually measures

Big-O answers "if I double the input size, roughly how much more work happens?" — not "how many milliseconds does this take." An O(n) algorithm on a fast machine can easily beat an O(1) algorithm on a slow one for small inputs; Big-O only describes the *shape* of the growth curve as `n` gets large, not a fixed speed.

```java
// O(1) -- constant time: cost doesn't depend on input size at all
int firstElement(int[] arr) {
    return arr[0]; // always exactly one operation, whether arr has 10 or 10 million elements
}
```

## Common complexity classes, from best to worst

- **O(1) — constant**: array index access, a HashMap `get`/`put` (Day 25) in the average case.
- **O(log n) — logarithmic**: binary search (Day 53) — each step throws away half the remaining input.
- **O(n) — linear**: a single loop over every element, like `Stream.filter` (Day 30) scanning a list once.
- **O(n log n) — linearithmic**: efficient comparison-based sorting, like merge sort (Day 55) and `Collections.sort`.
- **O(n²) — quadratic**: nested loops over the same input, like Day 50's `LinkedList.get(i)` inside a loop, or naive bubble sort (Day 54).
- **O(2ⁿ) — exponential**: naive recursive Fibonacci without memoization (Day 76) — the work roughly doubles with every additional input element.

```java
// O(n) -- linear: cost grows proportionally with input size
int sum(int[] arr) {
    int total = 0;
    for (int x : arr) { // one pass, n iterations
        total += x;
    }
    return total;
}

// O(n^2) -- quadratic: nested loop over the same input
boolean hasDuplicate(int[] arr) {
    for (int i = 0; i < arr.length; i++) {
        for (int j = i + 1; j < arr.length; j++) { // for each element, scan the rest again
            if (arr[i] == arr[j]) return true;
        }
    }
    return false;
}
```

## Rules for calculating Big-O

- **Drop constants**: an algorithm that does `2n` operations is still O(n) — the constant factor doesn't change the growth shape.
- **Drop lower-order terms**: O(n² + n) simplifies to O(n²) — for large `n`, the `n²` term dominates and the `n` term becomes irrelevant by comparison.
- **Different inputs get different variables**: an algorithm looping over array `a` (size `n`) and, separately, array `b` (size `m`) is O(n + m), not O(n) — they aren't the same input, so they can't share a variable.
- **Nested loops multiply; sequential loops add**: a loop inside a loop over the same input is O(n · n) = O(n²); two separate loops one after another are O(n) + O(n) = O(n), by the "drop constants" rule.

## Space complexity

Big-O also describes memory, not just time — how much *extra* space an algorithm needs, beyond the input itself, as a function of `n`.

```java
// O(1) extra space -- a fixed number of variables, regardless of input size
int sum(int[] arr) {
    int total = 0; // one variable, however big arr is
    for (int x : arr) total += x;
    return total;
}

// O(n) extra space -- allocates a new structure that grows with the input
int[] doubled(int[] arr) {
    int[] result = new int[arr.length]; // a second array, same size as the input
    for (int i = 0; i < arr.length; i++) result[i] = arr[i] * 2;
    return result;
}
```

There's often a genuine time/space tradeoff: a `HashSet` used to detect duplicates in O(n) time (Day 24) trades away the O(1) space of the nested-loop version above for O(n) extra space, in exchange for being dramatically faster.

## Best, worst, and average case

The same algorithm can have different Big-O bounds depending on the input. Linear search (Day 53) is O(1) best case (the very first element is the match), but O(n) worst case (the target is last, or absent, so every element must be checked) and O(n) average case too. Big-O by convention usually refers to the **worst case** unless stated otherwise, since that's the guarantee that actually matters for reliability — an algorithm that's usually fast but occasionally catastrophic is a real risk in production.

## Amortized analysis (a preview)

Some operations are expensive only occasionally, with the cost "amortized" (spread out) over many cheap calls. `ArrayList.add()` (Day 23) is O(1) *amortized*: most calls just place an element in existing space, but occasionally the backing array is full and must be resized (an O(n) copy) — averaged over many calls, the resizes are rare enough that the *average* cost per call stays O(1), even though any single call could hit that O(n) resize. Day 93 covers amortized analysis in more depth.
