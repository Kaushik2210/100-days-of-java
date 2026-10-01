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

`fastPower(2, 10)` makes only 4 recursive calls (exponents 10 → 5 → 2 → 1 → 0) instead of 10 sequential multiplications — the same halving-the-problem-size idea as binary search, applied to exponentiation instead of a sorted array.
