# Day 75: Backtracking — N-Queens & Permutations

Day 74's greedy algorithms commit to a choice and never look back. **Backtracking** is the opposite: it tries a choice, explores everything that follows from it, and if that path hits a dead end (or finishes), it **undoes** the choice and tries the next option. Structurally it's a depth-first search (Day 70) over the tree of all possible decisions, where "visited" is replaced by "choose, explore, un-choose."

## The choose / explore / un-choose template

Nearly every backtracking solution has the same three beats inside a loop over the available options:

1. **Choose** — make one decision and record it.
2. **Explore** — recurse (Day 52) to make all the *remaining* decisions.
3. **Un-choose** — reverse the decision so the next loop iteration starts from a clean state.

That third step is what separates backtracking from plain recursion: the shared state (a list being built, a board being filled) gets restored on the way back up, so every branch of the decision tree sees the correct state when it begins.

## Permutations: every ordering of a set

Generating all permutations of `[1, 2, 3]` means deciding, position by position, which unused element goes next.

```java
void permute(int[] nums, List<Integer> current, boolean[] used, List<List<Integer>> results) {
    if (current.size() == nums.length) {
        results.add(new ArrayList<>(current)); // snapshot a copy -- `current` keeps changing as we backtrack
        return;
    }

    for (int i = 0; i < nums.length; i++) {
        if (used[i]) continue;      // this element is already placed earlier in the current permutation

        used[i] = true;             // choose
        current.add(nums[i]);
        permute(nums, current, used, results); // explore
        current.remove(current.size() - 1);    // un-choose
        used[i] = false;
    }
}
```

The `new ArrayList<>(current)` copy matters: `current` is one list that gets mutated for the entire run, so storing a reference to it directly would leave every saved result pointing at the same list — all of them ending up empty once the recursion fully unwinds.

There are `n!` permutations, so any algorithm that generates all of them is at least O(n!) — backtracking doesn't beat that, it just generates them systematically without duplicates or gaps.

## Pruning: the real power of backtracking

Permutations need every branch, so nothing can be skipped. Many problems are different: a partial solution can be recognized as *already impossible* long before it's complete, and the whole subtree below it can be abandoned immediately. That early abandonment is called **pruning**, and it's what makes backtracking dramatically faster than blindly enumerating every complete candidate and checking it afterwards.

## N-Queens: place N queens on an N×N board with none attacking another

Queens attack along their row, column, and both diagonals. Place one queen per row, choosing a column for each; before placing, check whether that square is already under attack by an earlier queen — if so, skip it without exploring anything beneath it.

```java
int countNQueens(int n) {
    return place(0, n, new boolean[n], new boolean[2 * n], new boolean[2 * n]);
}

private int place(int row, int n, boolean[] columns, boolean[] diagonals, boolean[] antiDiagonals) {
    if (row == n) return 1; // all n queens placed without conflict -- one complete solution

    int count = 0;
    for (int col = 0; col < n; col++) {
        int diagonal = row - col + n;   // cells on the same "\" diagonal share row - col
        int antiDiagonal = row + col;   // cells on the same "/" diagonal share row + col

        if (columns[col] || diagonals[diagonal] || antiDiagonals[antiDiagonal]) continue; // prune: attacked square

        columns[col] = diagonals[diagonal] = antiDiagonals[antiDiagonal] = true;      // choose
        count += place(row + 1, n, columns, diagonals, antiDiagonals);                 // explore
        columns[col] = diagonals[diagonal] = antiDiagonals[antiDiagonal] = false;     // un-choose
    }
    return count;
}
```

The three boolean arrays make the "is this square attacked?" check O(1) instead of re-scanning every previously placed queen, using the arithmetic fact that all squares on one diagonal share the same `row - col` value, and all squares on one anti-diagonal share the same `row + col`.

Without pruning, brute force would consider every way to put `n` queens on `n²` squares — an astronomically larger space. Pruning cuts each branch the moment a placement conflicts, so the search only ever explores placements that are *still valid so far*. It remains exponential in the worst case, but is vastly faster in practice, which is why N-Queens is the standard first example of what backtracking buys.
