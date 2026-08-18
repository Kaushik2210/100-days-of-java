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
