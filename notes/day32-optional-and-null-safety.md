# Day 32: Optional & Null Safety

`null` is Java's traditional way to say "no value here" — but a `null` reference gives no signal in the type system that it might be absent; every caller has to remember to check, and forgetting produces a `NullPointerException` (as seen with unboxing in Day 28). `Optional<T>` is a container type that makes "this might not have a value" explicit and forces callers to deal with it.

## Creating an Optional

```java
Optional<String> present = Optional.of("hello");   // value must not be null
Optional<String> absent = Optional.empty();         // explicitly no value
Optional<String> maybe = Optional.ofNullable(getNameOrNull()); // wraps a possibly-null value safely
```

`Optional.of(null)` throws `NullPointerException` immediately — it's meant for values you already know are non-null. `Optional.ofNullable` is the safe entry point when the source might genuinely be `null`.

## Checking and unwrapping

```java
Optional<String> name = Optional.ofNullable(lookupName());

if (name.isPresent()) {
    System.out.println(name.get()); // only safe to call get() after checking isPresent()
}
```

Calling `.get()` on an empty `Optional` throws `NoSuchElementException` — so `isPresent()` + `get()` is really just `null`-checking with extra ceremony, and doesn't yet capture the real benefit of `Optional`. The idiomatic style, covered next, avoids `.get()` almost entirely.

## The functional style: map, filter, orElse

`Optional` supports the same `map`/`filter` vocabulary as `Stream` (Day 30), treating "a value or nothing" the same way a stream treats "zero or one elements." Chaining these lets you express a whole pipeline of "if present, transform; otherwise, fall back" without ever writing an explicit `if`.

```java
Optional<String> name = Optional.ofNullable(lookupName());

String greeting = name
    .map(n -> "Hello, " + n) // only runs if a value is present; otherwise stays empty
    .orElse("Hello, stranger"); // supplies a default if empty
System.out.println(greeting);
```

- **`orElse(fallback)`** — a plain default value, always evaluated eagerly (even when not needed).
- **`orElseGet(supplier)`** — a `Supplier<T>` (Day 29) that only runs if the `Optional` is actually empty; prefer this over `orElse` when computing the fallback is expensive.
- **`orElseThrow()`** / **`orElseThrow(exceptionSupplier)`** — throws if empty, useful when absence should genuinely be treated as an error at that point.
- **`ifPresent(consumer)`** — runs a `Consumer<T>` (Day 29) only if a value exists, doing nothing otherwise; a null-safe replacement for `if (x != null) { ... }`.
- **`filter(predicate)`** — keeps the value only if it matches; otherwise becomes empty, same as `Stream.filter`.

```java
Optional.ofNullable(lookupName())
    .filter(n -> n.length() > 2)
    .ifPresent(n -> System.out.println("Valid name: " + n));
```

## What Optional is for (and isn't)

`Optional` is designed for **return types** — signaling to a caller "this method might not have an answer" in a way the compiler and IDE make visible. It's generally discouraged as a field type or a method parameter type; for those, a plain nullable reference (with clear documentation) or a non-nullable default is preferred. And don't call `.get()` without first checking `isPresent()` (or better, skip both and use `map`/`orElse` instead) — that defeats the entire purpose of using `Optional` over plain `null` in the first place.
