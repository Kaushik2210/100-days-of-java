# Day 93: Amortized Analysis & Complexity Deep Dive

Big-O usually describes the cost of *one* operation in the worst case. That is too pessimistic for structures where an occasional operation is expensive but most are cheap. **Amortized analysis** bounds the *average cost per operation over a worst-case sequence*. It is a guarantee about any sequence of `n` operations, with no randomness and no assumption about the input.

Do not confuse it with average-case analysis. Average-case assumes a distribution over inputs. Amortized cost holds for the *worst* sequence you can build.

The code is in [`AmortizedAnalysisDemo.java`](../src/day93/AmortizedAnalysisDemo.java). Each structure counts its own elementary steps, so the claims below are measured, not just asserted.

## Three ways to prove an amortized bound

1. **Aggregate method**: bound the total cost of `n` operations, then divide by `n`.
2. **Accounting method**: charge each operation a fixed amount. Cheap operations overpay and store the surplus as *credit*. Expensive operations spend that credit. The bound holds as long as credit never goes negative.
3. **Potential method**: define a potential `Φ(state) ≥ 0`. Amortized cost = actual cost + `ΔΦ`. Summing over a sequence telescopes, so total actual cost ≤ total amortized cost.

## Example 1: the dynamic array

`ArrayList.add` is O(1) most of the time, but when the backing array is full it allocates a bigger one and copies every element.

**Aggregate:** with doubling, resizes copy `1 + 2 + 4 + ... + n/2 < n` elements in total. Add the `n` plain writes and the total is under `3n`, so each add costs at most 3 writes amortized.

**Accounting:** charge 3 per add. One pays for the write. Two are saved on the new element and on one old element. By the time the array fills, every element has the credit needed to pay for its own copy.

Measured:

```
n=    1,000  doubling: 2.02 writes/add (10 resizes)   x1.5: 3.12 writes/add   +1 each time: 500.5 writes/add
n=   10,000  doubling: 2.64 writes/add (14 resizes)   x1.5: 3.43 writes/add   +1 each time: 5000.5 writes/add
n=1,000,000  doubling: 2.05 writes/add (20 resizes)   x1.5: 3.10 writes/add
```

The point is the **growth rule**, not just "grow when full":

- **Multiplicative growth** (×2, ×1.5) gives O(1) amortized. Any factor `g > 1` works, costing about `1 + 1/(g - 1)` writes per add. Smaller factors waste less memory but copy more often.
- **Additive growth** (+1, or +k) copies `O(n)` elements every `k` adds, so the average cost is O(n/k), which is linear. The `+1` column shows it: 500 writes per add at n = 1,000 and 5,000 at n = 10,000.

The exact writes/add figure wobbles with where `n` lands relative to the last resize. That is why 10,000 sits above 1,000 for doubling, yet both stay under the bound of 3.

**Amortized O(1) does not mean every operation is O(1).** The demo finds the worst single add at n = 65,536: it costs 32,769 writes (add #32,769), while the average is 2.00. That spike is real. Amortization is fine for throughput and wrong for a hard real-time deadline.

## Example 2: the binary counter

Incrementing a `k`-bit counter flips a run of trailing 1s to 0 and one 0 to 1. A single increment can flip `k` bits (`0111...1 → 1000...0`), so the worst case is O(k).

**Aggregate:** bit 0 flips every increment, bit 1 every 2nd, bit 2 every 4th, and so on. Over `n` increments the total is `n + n/2 + n/4 + ... < 2n`.

**Potential:** let `Φ` = number of 1 bits. An increment that resets `t` ones and sets one zero has actual cost `t + 1`, and `ΔΦ = 1 - t`, so the amortized cost is `(t + 1) + (1 - t) = 2`.

```
n=       10  total flips=       18  per increment=1.800  (bound: 2n = 20)
n=    1,000  total flips=    1,994  per increment=1.994  (bound: 2n = 2,000)
n=1,000,000  total flips=1,999,993  per increment=2.000  (bound: 2n = 2,000,000)
```

## Example 3: a queue from two stacks

Enqueue pushes onto an `in` stack. Dequeue pops from an `out` stack, and when `out` is empty it first pours all of `in` into `out`, which reverses the order. One dequeue can move every element, so its worst case is O(n).

**Accounting:** charge 2 per enqueue: one for the push, one saved for the future move into `out`. Every element moves between stacks **at most once**, so total moves ≤ total enqueues, and every operation is O(1) amortized.

The demo checks this against a reference `ArrayDeque` over one million random operations (seed 93). The outputs matched, and the moves stayed below the enqueue count:

```
enqueues=550,138  element moves=464,205  (each element moves at most once, so moves <= enqueues: true)
```

Moves are fewer than enqueues because elements still sitting in `in` at the end have not moved yet.

## Reading complexity honestly

| Claim | Meaning | Where it breaks |
|-------|---------|-----------------|
| Worst case O(f) | every single operation is ≤ f | none, strongest guarantee |
| Amortized O(f) | any sequence of `n` ops costs ≤ n·f total | one operation can still be far above f |
| Expected O(f) | average over the algorithm's own randomness | a lucky or unlucky run can differ |
| Average case O(f) | average over an assumed input distribution | wrong if real inputs differ from the assumption |

Other places amortized bounds show up: `HashMap` rehashing (day 67), union-find with path compression (day 80), and heap-based structures such as the pairing and Fibonacci heaps.

## Takeaways

- Amortized analysis bounds the cost of a *sequence*, so a rare expensive operation can be paid for by many cheap ones.
- Pick a method by what is easiest to argue: aggregate for simple totals, accounting for "prepaying" stories, potential for state that evolves.
- Growth must be proportional. Doubling gives O(1) amortized appends, while adding a constant gives O(n).
- Amortized is not worst-case per operation. If latency spikes matter, check the single worst operation too.
