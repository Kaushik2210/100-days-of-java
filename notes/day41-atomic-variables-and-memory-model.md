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
