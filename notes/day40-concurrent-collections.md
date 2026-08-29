# Day 40: Concurrent Collections

`HashMap`, `ArrayList`, and friends (Days 23–25) are **not thread-safe** — concurrent reads and writes from multiple threads can corrupt their internal structure, not just produce wrong values. Wrapping every access in `synchronized` (Day 37) works but forces all threads through one lock, serializing what could otherwise run in parallel. `java.util.concurrent` provides purpose-built collections that stay correct under concurrent access with much better throughput.

## ConcurrentHashMap

`ConcurrentHashMap` is a drop-in, thread-safe replacement for `HashMap`. Instead of one lock for the whole map, it internally partitions locking across segments of the map, so unrelated updates from different threads can genuinely happen at the same time rather than queuing behind a single lock.

```java
Map<String, Integer> counts = new ConcurrentHashMap<>();

Runnable incrementer = () -> {
    for (int i = 0; i < 10_000; i++) {
        counts.merge("total", 1, Integer::sum); // atomic read-modify-write, safe across threads
    }
};

Thread t1 = new Thread(incrementer);
Thread t2 = new Thread(incrementer);
t1.start();
t2.start();
t1.join();
t2.join();

System.out.println(counts.get("total")); // always 20000 -- no race condition
```

A plain `HashMap` under this same concurrent access pattern wouldn't just risk lost updates like Day 36's counter — it can corrupt its internal bucket structure badly enough to throw exceptions or even loop forever in some JDK versions, which is why "just don't use HashMap concurrently" is a hard rule, not a performance suggestion.
