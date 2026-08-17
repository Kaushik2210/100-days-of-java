# Day 30: Streams API — map/filter/reduce

A `Stream` is a pipeline for processing a sequence of elements — not a data structure itself, but a chain of operations (transform, filter, aggregate) applied to data coming from a source like a collection. Streams let you describe *what* transformation you want instead of writing a manual loop for *how* to do it, using the functional interfaces from Day 29 as the building blocks.

## Creating a stream

Any `Collection` can produce a stream via `.stream()`. Streams can also be built directly from values or arrays.

```java
List<String> names = List.of("Asha", "Kiran", "Bo", "Ravi");
Stream<String> stream = names.stream();

Stream<Integer> literal = Stream.of(1, 2, 3);
```

A stream is consumed once — after a terminal operation runs, that stream object can't be reused; you'd need a fresh one from the source.

## map: transforming each element

`map` applies a `Function` (Day 29) to every element, producing a new stream of the transformed values, one-to-one.

```java
List<Integer> lengths = names.stream()
    .map(String::length) // Stream<String> -> Stream<Integer>
    .collect(Collectors.toList());
System.out.println(lengths); // [4, 5, 2, 4]
```

## filter: keeping only matching elements

`filter` takes a `Predicate` (Day 29) and keeps only the elements that return `true`, dropping the rest.

```java
List<String> longNames = names.stream()
    .filter(name -> name.length() > 3)
    .collect(Collectors.toList());
System.out.println(longNames); // [Asha, Kiran, Ravi]
```

`map` and `filter` are both **intermediate operations**: they return a new stream and don't actually run anything by themselves — nothing happens until a **terminal operation** (like `collect`, `forEach`, or `reduce`) triggers the whole pipeline to execute, element by element, lazily.
