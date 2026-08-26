# Day 37: Synchronization & Locks

Day 36 ended with a race condition: two threads calling `counter.increment()` 100,000 times each produced a total well under 200,000, because `count++` isn't a single atomic step — it's read, add one, write back, and two threads can interleave those steps and stomp on each other's update. `synchronized` is Java's built-in tool for making a section of code run by only one thread at a time.

## The synchronized keyword

Every Java object has an intrinsic lock (also called a "monitor lock"). Marking a method or block `synchronized` means a thread must acquire that lock before entering, and releases it automatically on exit — including if an exception is thrown. Any other thread trying to enter a section guarded by the same lock simply waits until it's free.

```java
class Counter {
    private int count;

    synchronized void increment() { // acquires `this`'s lock before running, releases it after
        count++;
    }

    synchronized int get() {
        return count;
    }
}
```

With `increment()` synchronized, two threads can no longer interleave the read-modify-write steps of `count++` — the second thread simply blocks until the first one finishes and releases the lock, so every increment is now atomic with respect to other synchronized access.

## Synchronized blocks

Synchronizing an entire method locks for its whole duration, which can be more than necessary. A `synchronized` block locks only the specific section that touches shared state, on an explicit object used purely as the lock:

```java
class Counter {
    private int count;
    private final Object lock = new Object(); // a dedicated lock object

    void increment() {
        synchronized (lock) { // only this block is protected, not the whole method
            count++;
        }
    }
}
```

Using a dedicated private lock object (rather than `this`) avoids surprising interactions with outside code that might also synchronize on the same object.
