# Day 100: Capstone Review, a DSA Roadmap Retrospective

Days 1 to 50 covered Java itself: syntax, OOP, collections, streams, concurrency, the JVM, patterns, JDBC, build tools, testing and profiling. Days 51 to 100 added the data structures and algorithms that sit under all of it. This last day looks back over the DSA half, pulls it into one map, and runs one program where several of the tools check each other.

## The map: what each tool is for

| Need | Tool | Days |
|---|---|---|
| Talk about cost | Big-O, amortized analysis | 51, 93 |
| Find an item | linear / binary search, hash table, trie | 53, 67, 68 |
| Put things in order | bubble/selection/insertion, merge, quick, heap sort | 54 to 57 |
| Sequences with cheap ends | linked lists, stacks, queues, deques | 58 to 62 |
| Ordered data, fast lookups | binary trees, BST, AVL | 63 to 65 |
| Always the best item next | binary heap, priority queue | 62, 66 |
| Relationships | graph representation, BFS, DFS | 69, 70 |
| Scan sorted or windowed data | two pointers, sliding window | 71, 72 |
| Split, choose, or explore | divide and conquer, greedy, backtracking | 73 to 75 |
| Overlapping subproblems | dynamic programming, knapsack, LCS, edit distance | 76 to 78, 92, 96 |
| Pack facts into bits | bit manipulation | 79 |
| Which things are connected | union-find | 80 |
| Weighted graphs | Dijkstra, Bellman-Ford, Kruskal, Prim | 81 to 84 |
| Order with dependencies | topological sort, bipartite checks | 85, 86 |
| Range questions | segment tree, Fenwick tree | 87, 88 |
| Text matching | KMP, Rabin-Karp | 89, 90 |
| Grids and matrices | matrix algorithms | 91 |
| Putting it to work | patterns, LRU cache, system design, interview practice | 94 to 99 |

## How to pick a tool

Most problems give themselves away through a few signals:

- **"Does X exist / how many times?"** points to a hash map or set.
- **"The k best / next smallest"** points to a heap.
- **"Sorted input"** suggests binary search or two pointers.
- **"Contiguous subarray or substring"** suggests a sliding window.
- **"All combinations"** suggests backtracking; if the same subproblems repeat, add memoization and it becomes DP.
- **"Shortest path"** suggests BFS if edges are unweighted, Dijkstra if weights are non-negative, Bellman-Ford if some are negative.
- **"Connected?" or "cheapest way to connect everything"** suggests union-find or an MST algorithm.
- **"Do A before B"** suggests topological sort.

## One program where the tools check each other

`src/day100/CapstoneDemo.java` generates 2,000 small random graphs and answers the same questions with different algorithms:

- Shortest distances from node 0: **Dijkstra** (a heap), **Bellman-Ford** (repeated relaxation), and **Floyd-Warshall** (a triple loop, not in the roadmap but easy to trust as a third opinion).
- Number of components: **union-find** against a plain **DFS**.
- Minimum spanning tree weight: **Kruskal** (sort plus union-find) against **Prim** (a heap), on the 734 graphs that happened to be connected.
- **Kahn's topological sort** on random DAGs hidden behind a shuffled labelling, checking that every edge points forward.

The run reported 0 mismatches in every comparison, and a small hand-worked graph gave the expected distances `[0, 3, 1, 4]` and MST weight 4. Algorithms built on completely different ideas agreeing on thousands of inputs is much stronger evidence than a few hand-picked examples. That is the habit these 100 days leaned on: write a trusted slow version, or a different algorithm, and compare.

Two honest limits. The graphs are tiny (at most 11 nodes), so this checks correctness, not speed. And Bellman-Ford here only sees positive weights, so its negative-edge behaviour (Day 82) is not exercised.

## Lessons that kept recurring

1. **Big-O describes growth, not speed.** Day 99 showed an O(n) bucket approach losing to an O(n log k) heap on a realistic input because of allocation overhead.
2. **Test against something independent.** Several wrong claims and bugs along the way were only caught by running the code and comparing it with a second method or a hand-computed answer.
3. **Edge cases are where solutions break:** empty input, one element, duplicates, ties, overflow.
4. **State the trade-off.** Nearly every structure buys time with memory, or simplicity with speed, or worst-case guarantees with constant factors.
5. **Measure carefully.** Warm-up, input shape and run-to-run variance can all make a timing table say the wrong thing.

## Where to go next

- Solve problems on a judge site, and for each one write the brute force first, then the improvement.
- Revisit the Java half with the DSA half in mind: `HashMap` (Day 25), `TreeMap`, `PriorityQueue` and `ArrayDeque` are the structures built here, tuned for production.
- Pick a project that needs both halves, such as a small search engine (tries, hashing, heaps) or a route planner (graphs, Dijkstra).
