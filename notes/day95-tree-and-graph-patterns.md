# Day 95: Problem-Solving Patterns — Trees & Graphs

Day 94 covered array and string patterns. Trees and graphs have their own small set of recurring shapes, and most interview-style problems are one of them in disguise. Today is seven patterns, each in [`TreeGraphPatternsDemo.java`](../src/day95/TreeGraphPatternsDemo.java) and each verified against an independent, simpler oracle on 2,000 random cases (fixed seed 95), with every comparison matching.

| # | Pattern | Reach for it when | Time |
|---|---------|-------------------|------|
| 1 | Level-order BFS | anything per level, shortest hops in an unweighted structure | O(n) |
| 2 | DFS that returns one value and records another | diameter, max path sum, balance checks | O(n) |
| 3 | Combine results from both subtrees | lowest common ancestor and "where do two things meet" | O(n) |
| 4 | Prefix sums along a root-to-node path | counting paths with a target sum | O(n) |
| 5 | Multi-source BFS | distance to the nearest of many sources | O(cells) |
| 6 | Flood fill | connected components on a grid | O(cells) |
| 7 | Topological order / cycle detection | dependencies, build or course ordering | O(V + E) |

Part 1 below covers the tree patterns. Part 2 covers the graph and grid patterns.

## Part 1: Trees

The demo builds trees two ways: `build` reads a heap-indexed array (children of index `i` at `2i + 1` and `2i + 2`, `null` for a missing node), and `randomTree` attaches each new node to a random free child slot so the tests see skinny, bushy and lopsided shapes.

### 1. Level-order BFS

A plain BFS visits nodes in level order but does not tell you where one level ends. The trick is to **snapshot the queue size** at the start of each pass and drain exactly that many nodes:

```java
while (!queue.isEmpty()) {
    int width = queue.size();          // nodes on this level
    for (int i = 0; i < width; i++) { /* poll, record, enqueue children */ }
}
```

`levels` on `[3, 9, 20, null, null, 15, 7]` returns `[[3], [9, 20], [15, 7]]`. The number of rows equals the height, and the total row size equals the node count, which the demo checks on 2,000 random trees.

### 2. DFS that returns one thing and records another

The **diameter** is the longest path (in edges) between any two nodes. It may not pass through the root. The O(n²) answer computes heights at every node. The one-pass answer notices that a path whose highest node is `n` has length `leftDepth + rightDepth`, and `depth()` already computes both:

```java
int l = depth(n.left, best);
int r = depth(n.right, best);
best[0] = Math.max(best[0], l + r);   // the answer: recorded on the side
return 1 + Math.max(l, r);            // what the parent needs: returned
```

The two outputs differ. A path can bend at `n`, but the parent can only extend one arm, so what is returned upward is the longer arm and not the sum. Mixing these up is the classic bug. This same shape solves maximum path sum and "is it height-balanced". The sample tree gives 3 (`9 → 3 → 20 → 15`), a three-node chain gives 2, and a single node gives 0.

### 3. Combine results from both subtrees (LCA)

The **lowest common ancestor** of `p` and `q` is the deepest node that has both as descendants (a node counts as its own descendant). The recursion asks each subtree "did you find `p` or `q`?":

```java
if (root == null || root == p || root == q) return root;
TreeNode left = lca(root.left, p, q), right = lca(root.right, p, q);
if (left != null && right != null) return root;   // found one on each side: this is the meeting point
return left != null ? left : right;               // otherwise pass the hit upward
```

Comparing by **reference** (`==`), not by value, matters: the random trees contain duplicate values, so a value comparison would match the wrong node. The oracle finds each node's root path and takes the last shared node. They agreed on 10,000 random node pairs.

### 4. Prefix sums along a path

Day 94's prefix-sum-plus-hash-map pattern works on trees if you treat each root-to-node path as an array. Keep a map of prefix sums for the *current path*, and count how many earlier prefixes equal `prefix - k`. The one new rule is **backtracking**: after both children are done, decrement this node's prefix in the map, so a sibling subtree never sees a path that does not lead to it.

```java
found += pathSums(n.left, ...) + pathSums(n.right, ...);
seen.merge(prefix, -1, Integer::sum);   // leaving this node
```

Forgetting that last line passes small tests and fails on bushy trees. The oracle starts a fresh running sum at every node (O(n²) in a skewed tree). On the textbook tree `[10, 5, -3, 3, 2, null, 11, 3, -2, null, 1]` with `k = 8` both find 3 paths. Over the random trees, 1,333 of the 2,000 contained at least one such path, so the check is not just comparing zeros.

## Part 2: Grids and graphs

A grid is a graph in disguise: each cell is a node and its four neighbours are the edges. The demo shares one `STEPS` array of the four directions, and every grid check uses the same bounds test before touching a neighbour.

### 5. Multi-source BFS

To find each cell's distance to the **nearest** of several sources, do not run one BFS per source (that is sources × cells). Put **every source in the queue at distance 0** before starting. The BFS wavefront then expands from all of them at once, and the first time a cell is reached is by its nearest source.

```java
for (int[] s : sources) { dist[s[0]][s[1]] = 0; queue.add(s); }
```

Marking a cell's distance when it is *enqueued* (not when it is dequeued) keeps each cell from entering the queue twice. Walls are `1`, and cells no source can reach stay `-1`. The sample grid has a wall column splitting the top two rows, so the middle cells are `-1` there while the bottom row reads `[2, 3, 3, 2]`. The oracle runs one BFS per source and takes the per-cell minimum; the two agreed on all 2,000 random grids.

### 6. Flood fill for connected components

Scan every cell. Each time you meet an unvisited land cell, that is a new component: count it, then flood the whole component so it is never counted again. Only the four side neighbours connect, so diagonal cells are separate (the sample map has 3 islands, not 2).

The demo uses an **explicit stack** instead of recursion. A recursive DFS on a 1500 × 1500 all-land grid would go over two million frames deep and throw `StackOverflowError` (day 92 measured how shallow the default stack really is). The explicit stack handles the same grid as one island without trouble. The oracle is union-find from day 80: merge each land cell with its right and lower land neighbours, then count roots. The two methods agreed on all 2,000 random grids.

Mark a cell as seen **when you push it**. Marking on pop lets the same cell be pushed several times by different neighbours.

### 7. Topological order and cycle detection

For "do A before B" constraints (build steps, course prerequisites), model them as a directed graph. A valid order exists exactly when the graph has no cycle. **Kahn's algorithm** repeatedly takes a node with no unmet prerequisites, emits it, and removes its outgoing edges. If it ends with fewer than `n` nodes emitted, the leftovers sit on or behind a cycle, so it returns `null`.

```java
for (int v : next.get(u)) if (--indegree[v] == 0) ready.add(v);
```

The oracle for "is there a cycle" is a different algorithm, DFS with three colors: white (unvisited), gray (on the current path), black (finished). Reaching a gray node means a back edge, which means a cycle. The demo also checks each returned order directly by confirming every edge goes from an earlier position to a later one. Over 2,000 random digraphs, 1,227 were cyclic and the rest produced valid orders; all agreed. Self-loops are included in the random edges, and both algorithms treat `u → u` as a cycle.

Kahn's order is not unique: `0 → 1, 0 → 2, 1 → 3, 2 → 3` gives `[0, 1, 2, 3]` here but `[0, 2, 1, 3]` is also valid.

## Choosing a pattern

1. A tree question about **a path or a size** below each node? Write a DFS that returns what the parent needs and records the answer on the side (pattern 2).
2. A question about **two nodes** meeting? Pass hits upward and answer where both sides report (pattern 3).
3. A question about **sums along a downward path**? Prefix map with backtracking (pattern 4).
4. A question phrased in **levels, rings, or minimum steps** on an unweighted structure? BFS, and use multi-source BFS when there are several starting points (patterns 1 and 5).
5. A question about **regions or groups**? Flood fill, or union-find if edges arrive over time (pattern 6).
6. A question about **order given dependencies**? Kahn's algorithm, and treat leftover nodes as a cycle (pattern 7).

## Takeaways

- Most tree DFS problems are one question: what does my parent need from me, and what do I record on the way?
- BFS gives fewest steps only when every edge costs the same; once edges are weighted, use Dijkstra (day 81).
- Mark nodes visited when they are **enqueued or pushed**, not when they are processed.
- Use an explicit stack when recursion depth can reach the size of the input.
- Each pattern is trusted here because a deliberately simple second implementation agreed with it. Write the slow, obvious version first.
