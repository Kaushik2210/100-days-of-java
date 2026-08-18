# Day 31: Streams API — Collectors & Grouping

Day 30 ended most pipelines with `.collect(Collectors.toList())`. `Collectors` is a whole toolkit of ready-made terminal strategies for turning a stream back into a concrete result — not just lists, but sets, maps, joined strings, and grouped/summarized structures.

## Basic collectors: toList, toSet, toMap, joining

```java
List<String> names = List.of("Asha", "Kiran", "Bo", "Ravi", "Asha");

List<String> asList = names.stream().collect(Collectors.toList());
Set<String> asSet = names.stream().collect(Collectors.toSet()); // duplicates removed

Map<String, Integer> nameToLength = names.stream()
    .distinct()
    .collect(Collectors.toMap(name -> name, String::length)); // key function, value function
System.out.println(nameToLength); // {Asha=4, Kiran=5, Bo=2, Ravi=4} (order not guaranteed)

String joined = names.stream()
    .distinct()
    .collect(Collectors.joining(", ", "[", "]")); // delimiter, prefix, suffix
System.out.println(joined); // [Asha, Kiran, Bo, Ravi]
```

`Collectors.toMap` throws `IllegalStateException` if two elements produce the same key — you must supply a third "merge function" argument if duplicate keys are expected and should be resolved rather than treated as an error.

## groupingBy: splitting a stream into buckets

`Collectors.groupingBy` is the stream equivalent of a `GROUP BY` in SQL: it classifies every element by a key function and produces a `Map<K, List<T>>` where each key maps to all the elements that produced it.

```java
List<String> words = List.of("apple", "banana", "avocado", "blueberry", "cherry");

Map<Character, List<String>> byFirstLetter = words.stream()
    .collect(Collectors.groupingBy(w -> w.charAt(0)));
System.out.println(byFirstLetter); // {a=[apple, avocado], b=[banana, blueberry], c=[cherry]}
```

## Downstream collectors: summarizing each group

A second argument to `groupingBy` — a **downstream collector** — changes what each group's value is, instead of always collecting to a `List`.

```java
Map<Character, Long> countByFirstLetter = words.stream()
    .collect(Collectors.groupingBy(w -> w.charAt(0), Collectors.counting()));
System.out.println(countByFirstLetter); // {a=2, b=2, c=1}

Map<Character, Integer> totalLengthByFirstLetter = words.stream()
    .collect(Collectors.groupingBy(w -> w.charAt(0), Collectors.summingInt(String::length)));
System.out.println(totalLengthByFirstLetter); // {a=12, b=15, c=6}
```

## partitioningBy: splitting into exactly two groups

When the classification is a `Predicate` (true/false) rather than an arbitrary key, `Collectors.partitioningBy` is more direct than `groupingBy` — it always produces a `Map<Boolean, List<T>>` with exactly two entries, even if one group ends up empty.

```java
Map<Boolean, List<String>> byLength = words.stream()
    .collect(Collectors.partitioningBy(w -> w.length() > 5));
System.out.println(byLength.get(true));  // [banana, avocado, blueberry, cherry]
System.out.println(byLength.get(false)); // [apple]
```
