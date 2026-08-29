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

## CopyOnWriteArrayList

`CopyOnWriteArrayList` takes a different strategy: every write (`add`, `remove`, `set`) copies the entire underlying array and swaps it in, so reads never need any locking at all and iterate over a stable, unchanging snapshot. Writes are relatively expensive (an O(n) copy every time), but reads are extremely cheap and never throw `ConcurrentModificationException` (Day 26) even if the list is modified mid-iteration.

```java
List<String> subscribers = new CopyOnWriteArrayList<>();
subscribers.add("alice@example.com");
subscribers.add("bob@example.com");

for (String subscriber : subscribers) { // safe even if another thread adds/removes concurrently
    System.out.println("Notifying " + subscriber);
}
```

This makes it a good fit for read-heavy, write-rare scenarios — a list of event listeners or subscribers that's iterated constantly but modified only occasionally.

## BlockingQueue: coordinating producer/consumer threads

`BlockingQueue<E>` extends `Queue` with methods that **block** instead of failing when the queue can't immediately satisfy the operation: `put(item)` blocks while the queue is full (for a bounded queue), and `take()` blocks while the queue is empty, waiting until another thread adds something. This makes it the standard tool for a producer/consumer pipeline.

```java
BlockingQueue<Integer> queue = new LinkedBlockingQueue<>(10); // capacity 10

Thread producer = new Thread(() -> {
    for (int i = 0; i < 5; i++) {
        try {
            queue.put(i); // blocks if the queue is full
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
});

Thread consumer = new Thread(() -> {
    for (int i = 0; i < 5; i++) {
        try {
            System.out.println("Consumed: " + queue.take()); // blocks if the queue is empty
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
});
```

`ExecutorService` (Day 38) is actually built on top of exactly this pattern internally — a `BlockingQueue` holding submitted tasks, with worker threads calling `take()` in a loop to pull work off it.
