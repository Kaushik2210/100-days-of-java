# Day 43: Garbage Collection Algorithms & Tuning

Day 42 established that every object lives on the heap. Java never requires manually freeing that memory (unlike languages with explicit `free`/`delete`) — instead, the **garbage collector (GC)** periodically identifies objects that are no longer reachable from any running code and reclaims their memory automatically.

## Reachability: what makes an object collectible

An object becomes eligible for garbage collection once nothing reachable from a "GC root" (local variables on any thread's stack, static fields, active JNI references) still references it — directly or transitively. Setting the last reference to `null`, or simply letting a local variable go out of scope, can make an object eligible.

```java
Object obj = new byte[1_000_000]; // reachable via the local variable `obj`
obj = null; // no more references anywhere -- now eligible for collection
```

Importantly, becoming *eligible* doesn't mean *immediately collected* — the GC decides when to actually run, based on its own algorithm and memory pressure, not the instant a reference is dropped. `System.gc()` only *suggests* a collection; the JVM is free to ignore the request entirely.

## The generational hypothesis

Most GC algorithms exploit an observed pattern: **most objects die young**. The heap is split into generations:

- **Young generation** — where new objects are allocated. Collected frequently, quickly, because most objects here are already garbage by the time a collection runs (short-lived temporaries, loop-local objects).
- **Old (tenured) generation** — objects that survive several young-generation collections get "promoted" here. Collected less often, since these tend to be long-lived (caches, singletons, the objects backing your running application).

This split means the GC doesn't have to scan the entire heap on every pass — a young-generation ("minor") collection is fast precisely because it only looks at the small region where most garbage actually accumulates.

## GC algorithms

The JVM ships several collectors, trading throughput, pause time, and memory overhead differently:

- **Serial GC** — a single-threaded collector that stops the entire application while it runs ("stop-the-world"). Simple and low-overhead, appropriate for small heaps or single-core environments where pause time barely matters.
- **Parallel GC** — like Serial, but uses multiple threads to collect faster, still stop-the-world. Historically Java's default; optimizes for overall throughput over pause latency.
- **G1 (Garbage-First) GC** — the default since Java 9. Divides the heap into many small regions and collects the ones with the most garbage first, aiming for predictable, short pauses even on large heaps rather than one long stop-the-world pass.
- **ZGC / Shenandoah** — newer, low-latency collectors designed to keep pause times in the single-digit milliseconds even on very large (multi-gigabyte) heaps, at some cost to raw throughput.

The right choice depends on the workload: a batch job that just needs to finish fast might prefer Parallel GC's throughput; a latency-sensitive web service serving live traffic usually wants G1 or ZGC to avoid noticeable pause spikes.

## Common tuning flags

- **`-Xms<size>`** — initial heap size (e.g. `-Xms512m`).
- **`-Xmx<size>`** — maximum heap size (e.g. `-Xmx2g`). Hitting this ceiling with no reclaimable memory left is what produces `OutOfMemoryError: Java heap space` (Day 42).
- **`-XX:+UseG1GC`** / **`-XX:+UseZGC`** / **`-XX:+UseParallelGC`** — explicitly select a collector, overriding the JVM's default choice.
- **`-Xlog:gc`** — enables GC logging, showing when collections run, how long they take, and how much memory they reclaim — the starting point for diagnosing GC-related pauses in a real application.

Tuning is something to reach for only after measuring an actual problem (long pauses, high CPU spent on GC) — not a default step for every program, since the out-of-the-box defaults (G1, sensible heap sizing) are well-suited to the vast majority of applications.
