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

## ReentrantLock: an explicit alternative

`java.util.concurrent.locks.ReentrantLock` provides the same mutual-exclusion guarantee as `synchronized`, but as an object you control explicitly — lock and unlock are separate calls, which gives more flexibility (try-locking with a timeout, checking whether the lock is held, allowing a thread to be interrupted while waiting) at the cost of needing to remember to unlock manually.

```java
class Counter {
    private int count;
    private final ReentrantLock lock = new ReentrantLock();

    void increment() {
        lock.lock();
        try {
            count++;
        } finally {
            lock.unlock(); // MUST be in finally -- unlocking is never automatic like synchronized
        }
    }
}
```

The `finally` block is not optional: if the protected code throws and the lock is never released in a `finally`, every other thread waiting on that lock blocks forever. `synchronized` releases its lock automatically on any exit path (normal return or exception), which is exactly why it's the simpler default choice — reach for `ReentrantLock` only when you need one of its extra capabilities.

## Deadlock

A deadlock happens when two or more threads each hold a lock the other needs, and neither can proceed — thread A holds lock 1 and waits for lock 2, while thread B holds lock 2 and waits for lock 1. Neither ever releases, so both wait forever.

```java
// Thread A: synchronized(lock1) { ... synchronized(lock2) { ... } }
// Thread B: synchronized(lock2) { ... synchronized(lock1) { ... } }
// If A acquires lock1 and B acquires lock2 at the same moment, both threads block forever
```

The simplest defense is **consistent lock ordering**: if every thread in a program always acquires multiple locks in the same fixed order (e.g. always lock1 before lock2, never the reverse), the circular-wait condition above can't occur.
