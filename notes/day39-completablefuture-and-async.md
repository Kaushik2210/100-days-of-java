# Day 39: CompletableFuture & Async Programming

Day 38's `Future.get()` has one big limitation: it **blocks** the calling thread until the result is ready — there's no way to say "run this callback automatically when the result arrives" without a thread sitting idle waiting for it. `CompletableFuture<T>` (Java 8) fixes this: it supports chaining callbacks that run asynchronously when a result becomes available, without ever blocking to "check."

## Creating and consuming a CompletableFuture

```java
CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> {
    sleepQuietly(100); // simulate work
    return 21 * 2;
}); // runs on a background thread (the common ForkJoinPool by default) -- returns immediately

future.thenAccept(result -> System.out.println("Result: " + result)); // runs automatically when ready, no blocking
```

`supplyAsync` takes a `Supplier<T>` (Day 29) and runs it on a background thread pool, returning immediately with a `CompletableFuture` representing the eventual result. `thenAccept` registers a callback that fires as soon as that result is available — the calling thread never has to sit and wait for it the way `Future.get()` forces.

## Transforming the result: thenApply

`thenApply` is `CompletableFuture`'s equivalent of `Stream.map` (Day 30) — it transforms the eventual value, once available, into something else, without blocking to do it.

```java
CompletableFuture<String> chained = CompletableFuture
    .supplyAsync(() -> 21 * 2)
    .thenApply(n -> "The answer is " + n); // transforms the result once it arrives

System.out.println(chained.join()); // join() blocks -- fine at the very end of a pipeline, in a demo/main method
```

`join()` (like `get()`) does block, and is used here only because `main` has nothing else useful to do while waiting — in real async code, the whole point is to keep chaining callbacks instead of blocking.

## thenCompose: chaining dependent async steps

When one async operation's result feeds into *another* async operation (rather than a plain synchronous transformation), `thenApply` would produce a nested `CompletableFuture<CompletableFuture<T>>` — awkward to use. `thenCompose` flattens this automatically, the same role `flatMap` plays for streams and `Optional`.

```java
CompletableFuture<String> userName = CompletableFuture.supplyAsync(() -> 42); // fetch a user ID
CompletableFuture<String> profile = userName.thenCompose(id ->
    CompletableFuture.supplyAsync(() -> "Profile for user " + id) // another async call, using the first result
);
System.out.println(profile.join());
```

## thenCombine: combining two independent async results

When two async operations run independently and both results are needed together, `thenCombine` waits for both and merges them with a `BiFunction` (Day 29).

```java
CompletableFuture<Integer> price = CompletableFuture.supplyAsync(() -> 100);
CompletableFuture<Double> taxRate = CompletableFuture.supplyAsync(() -> 0.08);

CompletableFuture<Double> total = price.thenCombine(taxRate, (p, rate) -> p * (1 + rate));
System.out.println(total.join()); // 108.0
```

## Exception handling: exceptionally and handle

If the supplier passed to `supplyAsync` throws, that exception propagates through the chain instead of any of the `then...` callbacks running — much like an uncaught exception skipping past ordinary code. `exceptionally` supplies a fallback value if (and only if) something failed upstream; `handle` runs regardless of success or failure, receiving both the result and the exception (one of which is always `null`).

```java
CompletableFuture<Integer> risky = CompletableFuture.supplyAsync(() -> {
    if (true) throw new RuntimeException("boom");
    return 42;
});

CompletableFuture<Integer> recovered = risky.exceptionally(ex -> {
    System.out.println("Recovered from: " + ex.getMessage());
    return -1; // fallback value used instead
});
System.out.println(recovered.join()); // -1
```
