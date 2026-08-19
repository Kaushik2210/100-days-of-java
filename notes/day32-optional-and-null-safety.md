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
