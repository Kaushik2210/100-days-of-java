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
