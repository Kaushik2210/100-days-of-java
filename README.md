# 100 Days of Java

**A complete, hands-on path from "Hello, World" to data structures, algorithms and interview prep, in 100 daily lessons.**

[![CI](https://github.com/Kaushik2210/100-days-of-java/actions/workflows/ci.yml/badge.svg)](https://github.com/Kaushik2210/100-days-of-java/actions/workflows/ci.yml)
[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](./LICENSE)
![Java 17+](https://img.shields.io/badge/Java-17%2B-orange)
![Days](https://img.shields.io/badge/days-100%20of%20100-brightgreen)

Every day is a short written lesson plus a runnable Java program. The programs print their results next to the expected values, and the algorithm days check themselves against a brute-force or independent method on thousands of random inputs. The notes only claim what the code demonstrates, and several claims were corrected along the way after running the code showed they were wrong.

## What you will find

| Days | Phase | Topics |
|---|---|---|
| 1 to 10 | Foundations | syntax, types, operators, control flow, loops, arrays, strings, methods, constructors |
| 11 to 20 | Object-oriented programming | inheritance, polymorphism, abstraction, interfaces, encapsulation, packages, enums, inner classes |
| 21 to 28 | Core APIs and error handling | exceptions, collections (List, Set, Map), iterators and comparators, generics, wrapper classes |
| 29 to 35 | Modern Java | lambdas, streams, `Optional`, date and time, file I/O, serialization |
| 36 to 41 | Concurrency | threads, synchronization, executors, `CompletableFuture`, concurrent collections, atomics and the memory model |
| 42 to 50 | Expert topics | JVM internals, garbage collection, design patterns, JDBC, Maven and Gradle, JUnit and Mockito, profiling |
| 51 to 60 | DSA foundations | Big-O, recursion, searching, sorting, linked lists, stacks |
| 61 to 70 | Core data structures | queues, priority queues, trees, BST, AVL, heaps, hash tables, tries, graphs and traversal |
| 71 to 80 | Algorithmic techniques | two pointers, sliding window, divide and conquer, greedy, backtracking, dynamic programming, bit manipulation, union-find |
| 81 to 88 | Graph algorithms and advanced trees | Dijkstra, Bellman-Ford, Kruskal, Prim, topological sort, bipartite checks, segment and Fenwick trees |
| 89 to 96 | Problem-solving patterns | KMP, Rabin-Karp, matrices, memoized vs iterative DP, amortized analysis, array, tree, graph and DP patterns |
| 97 to 100 | Capstone and interview prep | LRU cache, system design basics, mock interviews, roadmap retrospective |

The full day-by-day plan is in [`CURRICULUM.md`](./CURRICULUM.md), and [`PROGRESS.md`](./PROGRESS.md) links every day to its notes and code.

## Highlights

- **Day 57, [heap sort](./notes/day57-heap-sort.md)** and the sorting days: algorithms compared on correctness first, speed second.
- **Day 81 to 84, [shortest paths and spanning trees](./notes/day81-dijkstra.md)**: Dijkstra, Bellman-Ford, Kruskal and Prim.
- **Day 97, [an LRU cache built from scratch](./notes/day97-lru-cache.md)**.
- **Day 98, [system design basics](./notes/day98-system-design-basics.md)**: estimation, base62 IDs, consistent hashing, a token bucket.
- **Day 99, [mock interview and trade-offs](./notes/day99-mock-interview-and-tradeoffs.md)**: including a case where the O(n) solution is the slowest.
- **Day 100, [capstone](./notes/day100-dsa-roadmap-retrospective.md)**: six independent graph algorithms agreeing on 2,000 random graphs.

## Quick start

You need JDK 17 or newer.

```bash
git clone https://github.com/Kaushik2210/100-days-of-java.git
cd 100-days-of-java
mkdir -p out
javac -sourcepath src -d out src/day100/CapstoneDemo.java
java -cp out CapstoneDemo
```

Swap `day100` and `CapstoneDemo` for any day listed in [`PROGRESS.md`](./PROGRESS.md). Day 49 (JUnit and Mockito) also needs those libraries on the classpath; its notes explain how.

## Repository layout

```
notes/      the lesson for each day (notes/dayNN-topic.md)
src/        the runnable program for each day (src/dayNN/ClassName.java)
CURRICULUM.md   the 100-day roadmap
PROGRESS.md     every day linked to its notes and code
```

## Contributing

Corrections, clearer explanations and extra tests are welcome. See [`CONTRIBUTING.md`](./CONTRIBUTING.md).

## License

Released under the [MIT License](./LICENSE). Use it, learn from it, and share it.
