# Day 96: Problem-Solving Patterns — Dynamic Programming

Days 76 to 78 and Day 92 taught how dynamic programming works: overlapping subproblems, a recurrence, and a table (or a cache). The harder skill in practice is **recognizing** that a new problem is a DP problem and quickly choosing the right shape. This day collects the handful of shapes that cover most DP problems you will meet, each with a small worked example.

## A five-question framework

Almost every DP solution answers the same five questions. Answering them *in writing* before coding prevents most mistakes.

1. **State:** what does one table entry mean? ("the best value using the first `i` items", "the answer for the substring `s[i..j]`".) This is the most important question; a vague state gives a vague recurrence.
2. **Recurrence:** how does an entry follow from smaller entries? Usually it is a choice (take or skip, extend or restart) and you pick the best.
3. **Base cases:** which entries are known without computing?
4. **Order:** in what order can entries be filled so each one's inputs are ready? (Or let memoization decide, Day 92.)
5. **Answer:** which entry, or which combination of entries, is the final answer? It is not always the last cell.

Then check for space reductions: if an entry only needs the previous one or two, the table collapses to a few variables.

## Pattern 1: linear choices (take it or skip it)

Process items left to right and decide for each whether to take it, where taking one rules out its neighbor. The classic is **House Robber**: given the amount of money in each house in a row, find the maximum you can take if you may not rob two adjacent houses.

- **State:** `best[i]` = the most money obtainable from the first `i` houses.
- **Recurrence:** either skip house `i` (keep `best[i-1]`), or rob it and add it to the best result that ended *two* houses back: `best[i] = max(best[i-1], best[i-2] + money[i])`.
- **Base cases:** `best[0] = 0`, and `best[1] = money[0]`.
- **Answer:** `best[n]`.

Each entry only reads the two before it, so two variables suffice:

```java
int rob(int[] money) {
    int twoBack = 0;        // best for the first i-2 houses
    int oneBack = 0;        // best for the first i-1 houses
    for (int amount : money) {
        int current = Math.max(oneBack, twoBack + amount);   // skip this house, or rob it
        twoBack = oneBack;
        oneBack = current;
    }
    return oneBack;
}
```

This is O(n) time and O(1) space. The same skeleton, "best of skipping or taking-after-a-gap", underlies a whole family: climbing stairs (Day 92), decoding strings into letters, and maximum non-adjacent sums.

**Recognizing it:** the input is a sequence, you make a decision per element, and the decision only interacts with a *bounded* number of previous elements.
