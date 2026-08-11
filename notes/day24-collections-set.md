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

## LinkedHashSet

`LinkedHashSet` is a middle ground: it has the same O(1) average performance as `HashSet`, but additionally maintains a linked list through the entries so iteration order matches **insertion order**. Use it when you want set semantics (no duplicates) but still need predictable, repeatable iteration order.

## TreeSet

`TreeSet` is backed by a red-black tree and keeps its elements in **sorted order** at all times — every `add` inserts into the correct sorted position. Basic operations become O(log n) instead of O(1), trading some speed for always-sorted iteration.

```java
Set<Integer> scores = new TreeSet<>();
scores.add(85);
scores.add(42);
scores.add(67);
System.out.println(scores); // [42, 67, 85] -- always sorted, regardless of insertion order
```

For elements without a "natural" ordering (or to sort them differently), pass a `Comparator` to the constructor:

```java
Set<String> byLength = new TreeSet<>((a, b) -> a.length() - b.length());
byLength.add("kiwi");
byLength.add("fig");
byLength.add("apple");
System.out.println(byLength); // sorted by string length: [fig, kiwi, apple]
```

`TreeSet` also implements `NavigableSet`, adding methods like `first()`, `last()`, `higher(e)`, and `lower(e)` for range-style queries that neither `HashSet` nor `LinkedHashSet` can offer.

## Choosing between them

- **No ordering guarantee needed, want the fastest option** → `HashSet`.
- **Need iteration order to match insertion order** → `LinkedHashSet`.
- **Need elements kept sorted, or range queries like "everything greater than X"** → `TreeSet`.
