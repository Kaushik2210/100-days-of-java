# Day 41: Atomic Variables & the Java Memory Model (volatile)

Days 37 and 40 fixed race conditions with locks (`synchronized`, `ReentrantLock`) — correct, but every thread has to wait its turn even for something as simple as incrementing a counter. `java.util.concurrent.atomic` offers lock-free alternatives for single-variable updates, built on a hardware-level primitive instead of blocking.

## AtomicInteger, AtomicLong, and compare-and-swap

`AtomicInteger` wraps an `int` and provides methods like `incrementAndGet()` that are atomic without ever taking a lock. Internally, these rely on **compare-and-swap (CAS)**: a CPU instruction that atomically says "update this memory location to a new value, but only if it still holds the value I last read" — if another thread changed it in between, the CAS fails and the operation retries automatically.

```java
AtomicInteger counter = new AtomicInteger(0);

Runnable incrementer = () -> {
    for (int i = 0; i < 100_000; i++) {
        counter.incrementAndGet(); // atomic, lock-free
    }
};

Thread t1 = new Thread(incrementer);
Thread t2 = new Thread(incrementer);
t1.start();
t2.start();
t1.join();
t2.join();

System.out.println(counter.get()); // always 200000 -- same correctness as Day 37's synchronized version
```

For the common case of "many threads incrementing/updating one shared number," `AtomicInteger` is typically faster than `synchronized` under contention, because a failed CAS just retries instead of making a thread block and wait for a lock to be released by the OS scheduler.

## The Java Memory Model: visibility, not just atomicity

Every race condition so far has been about **atomicity** — multiple threads stepping on each other mid-update. There's a separate, subtler problem: **visibility**. For performance, the JVM and CPU are allowed to cache a variable's value per-thread (in a register or CPU cache) rather than always reading straight from main memory. Without an explicit signal that a value must be shared, one thread's write might simply never become visible to another thread — not because of a race on timing, but because the reading thread is looking at a stale, cached copy indefinitely.

```java
class Flag {
    boolean running = true; // no visibility guarantee across threads
}
```

```java
Flag flag = new Flag();
Thread worker = new Thread(() -> {
    while (flag.running) {
        // busy work
    }
    System.out.println("Stopped");
});
worker.start();
Thread.sleep(100);
flag.running = false; // this write is NOT guaranteed to ever be seen by the worker thread
```

On some JVMs/hardware this loop can run forever — the worker thread may keep reading a cached `true`, never noticing the write from the main thread, because nothing forces it to re-check main memory.

## volatile: guaranteeing visibility

Marking a field `volatile` tells the JVM: every read of this field must come from main memory (never a stale per-thread cache), and every write must be immediately flushed to main memory, visible to all other threads right away.

```java
class Flag {
    volatile boolean running = true; // every thread always sees the latest write
}
```

With `running` marked `volatile`, the worker thread above is guaranteed to observe `flag.running = false` and exit its loop reliably.

## volatile vs synchronized/Atomic: what it does and doesn't fix

`volatile` solves **visibility only** — it does not make compound operations atomic. `count++` on a `volatile int` is still three separate steps (read, add, write) and can still lose updates under concurrent access, exactly like Day 36's original race condition. Use `volatile` for a single flag or reference read/written independently by multiple threads (like the stop-flag above); use `AtomicInteger`/`AtomicLong`/etc. when the operation is a genuine read-modify-write; use `synchronized`/`ReentrantLock` (Day 37) when multiple related fields must be updated together as one atomic unit.
