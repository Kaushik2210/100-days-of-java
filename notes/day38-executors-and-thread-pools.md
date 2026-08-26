# Day 38: Executors & Thread Pools

Creating a `new Thread(...)` for every unit of work (Day 36) doesn't scale — each OS thread has real overhead (memory for its stack, scheduling cost), and nothing stops a program from accidentally spawning thousands of them under load. The `Executor` framework decouples "submitting a task" from "how and when it actually runs," typically backed by a reusable pool of worker threads.

## ExecutorService and thread pools

`Executors` provides factory methods for common pool shapes; `ExecutorService` is the interface you submit work to.

```java
ExecutorService pool = Executors.newFixedThreadPool(4); // 4 worker threads, reused across tasks

for (int i = 0; i < 10; i++) {
    int taskNumber = i;
    pool.submit(() -> {
        System.out.println("Task " + taskNumber + " on " + Thread.currentThread().getName());
    });
}

pool.shutdown(); // stops accepting new tasks; already-submitted ones still run to completion
```

Ten tasks submitted to a 4-thread pool run four at a time — as each worker thread finishes a task, it picks up the next queued one, instead of the program spawning ten separate OS threads for ten pieces of work.

## Common pool types

- **`Executors.newFixedThreadPool(n)`** — a fixed number of threads, reused indefinitely; a solid general-purpose default.
- **`Executors.newCachedThreadPool()`** — creates threads on demand and reuses idle ones, growing and shrinking with load; suited to many short-lived tasks.
- **`Executors.newSingleThreadExecutor()`** — exactly one worker thread, running submitted tasks strictly one at a time, in order — useful when tasks must never run concurrently with each other.

## Shutting down

`shutdown()` lets already-submitted tasks finish but rejects new ones. `shutdownNow()` attempts to stop everything immediately, including interrupting running tasks, and returns the tasks that never started. An `ExecutorService` that's never shut down keeps its threads alive indefinitely, which is a common way to accidentally leave a program running after `main` "finishes."
