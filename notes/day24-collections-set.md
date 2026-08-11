# Day 24: Collections Framework — Set (HashSet, TreeSet)

Where `List` allows duplicates and preserves insertion order, `Set<E>` guarantees the opposite: no duplicates, ever. Adding an element that's already present (per `equals()`/`hashCode()`, from Day 17) is a no-op — the set silently stays the same size.

## The Set interface

```java
Set<String> tags = new HashSet<>();
tags.add("java");
tags.add("backend");
tags.add("java"); // already present -- ignored, size stays 2
System.out.println(tags.size()); // 2
System.out.println(tags.contains("java")); // true
```

## HashSet

`HashSet` is backed by a `HashMap` internally, so lookups, insertions, and removals are all O(1) on average — but it makes **no guarantee about iteration order**. The order you see when printing or iterating a `HashSet` depends on hash bucket placement, not insertion order, and can even change between runs.

```java
Set<String> letters = new HashSet<>();
letters.add("c");
letters.add("a");
letters.add("b");
System.out.println(letters); // order is unspecified -- don't rely on it
```

`HashSet` relies entirely on `equals()`/`hashCode()` to detect duplicates, which is why Day 17's rule — override both together, consistently — matters just as much for sets as it does for `HashMap` keys.
