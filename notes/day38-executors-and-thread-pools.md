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

`awaitTermination(timeout, unit)` blocks the calling thread until either every task has finished or the timeout elapses — useful right after `shutdown()` when the rest of the program needs to know the pool is genuinely done, similar in spirit to `Thread.join()` from Day 36 but for an entire pool at once.

## Callable and Future: getting a result back

`Runnable` (Day 36) returns nothing and can't throw a checked exception. `Callable<V>` is its result-bearing counterpart: it returns a value and is allowed to throw. Submitting a `Callable` to an `ExecutorService` returns a `Future<V>` immediately — a placeholder for a result that isn't ready yet.

```java
ExecutorService pool = Executors.newFixedThreadPool(2);

Callable<Integer> task = () -> {
    Thread.sleep(100); // simulate work
    return 21 * 2;
};

Future<Integer> future = pool.submit(task); // returns immediately -- doesn't block

System.out.println("Doing other work while the task runs...");
Integer result = future.get(); // blocks here until the result is ready
System.out.println("Result: " + result);

pool.shutdown();
```

`future.get()` blocks until the task completes (or throws the task's exception, wrapped in `ExecutionException`), or accepts a timeout overload (`future.get(1, TimeUnit.SECONDS)`) to avoid waiting forever. This is the traditional way to run something in the background and collect its result later; Day 39 (`CompletableFuture`) builds a much richer, non-blocking API on top of this same idea.
