# Day 36: Threads & the Runnable Interface

Every Java program starts with one thread of execution (the `main` thread). A `Thread` lets a program run additional code concurrently — genuinely in parallel on a multi-core machine, or interleaved by the OS scheduler on a single core — instead of doing everything strictly one step after another.

## Two ways to define a thread's work

**Extend `Thread`** and override `run()`:

```java
class GreetingThread extends Thread {
    @Override
    public void run() {
        System.out.println("Hello from " + Thread.currentThread().getName());
    }
}
```

**Implement `Runnable`** and pass it to a `Thread` — the preferred approach, since Java only allows single inheritance (Day 20) and a class implementing `Runnable` can still extend something else, plus it cleanly separates "the work" from "the thing that runs it":

```java
class GreetingTask implements Runnable {
    @Override
    public void run() {
        System.out.println("Hello from " + Thread.currentThread().getName());
    }
}
```

```java
Thread t1 = new GreetingThread();
Thread t2 = new Thread(new GreetingTask());
Thread t3 = new Thread(() -> System.out.println("Hello from a lambda thread")); // Runnable is functional (Day 29)

t1.start();
t2.start();
t3.start();
```

## start() vs run()

Calling `start()` asks the JVM to spin up a brand-new OS-level thread, which then calls `run()` **on that new thread**. Calling `run()` directly just executes it like a normal method call, on the current thread — no concurrency happens at all. This is one of the most common beginner mistakes: `t1.run()` compiles fine and looks correct, but never actually starts a second thread.

## Thread lifecycle and join()

A thread moves through states: **New** (created, not yet started) → **Runnable** (started, competing for CPU time) → **Blocked/Waiting** (paused, e.g. waiting on a lock or another thread) → **Terminated** (`run()` has returned). Once terminated, a thread can never be started again — calling `start()` twice throws `IllegalThreadStateException`.

Because threads run concurrently, the calling code doesn't automatically wait for a spawned thread to finish. `join()` blocks the calling thread until the target thread terminates — essential whenever subsequent code depends on a thread's work being done.

```java
Thread worker = new Thread(() -> {
    System.out.println("Working...");
});
worker.start();
worker.join(); // main thread waits here until worker finishes
System.out.println("Worker is done"); // guaranteed to print after "Working..."
```

Without the `join()`, "Worker is done" could print before, after, or interleaved with "Working..." — there's no guarantee at all.

## Thread.sleep()

`Thread.sleep(millis)` pauses the *current* thread for at least the given duration, yielding the CPU to other threads. It throws the checked `InterruptedException` (Day 21), since another thread can interrupt a sleeping thread early.

```java
System.out.println("Before sleep");
Thread.sleep(1000); // pauses this thread for ~1 second
System.out.println("After sleep");
```

## A preview of the problem Day 37 solves

Multiple threads reading and writing the *same* shared data without coordination can produce a **race condition** — the final result depends on unpredictable timing, and different runs can produce different (wrong) answers even though each individual thread's code looks correct in isolation. Day 37 (Synchronization & Locks) covers the tools for preventing this.
