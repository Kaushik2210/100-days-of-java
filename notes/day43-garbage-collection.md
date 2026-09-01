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
