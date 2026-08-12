# Day 25: Collections Framework — Map (HashMap, TreeMap)

A `Map<K, V>` stores key-value pairs, where each key maps to exactly one value and keys are unique (adding a key that already exists overwrites its value). Unlike `List` and `Set`, `Map` doesn't extend `Collection` at all — it's a separate branch of the framework, but it's used just as often.

## The Map interface

```java
Map<String, Integer> ages = new HashMap<>();
ages.put("Asha", 30);
ages.put("Kiran", 25);
ages.put("Asha", 31); // overwrites the previous value for "Asha"

System.out.println(ages.get("Asha"));       // 31
System.out.println(ages.get("Unknown"));    // null -- key not present
System.out.println(ages.containsKey("Kiran")); // true
```

Iterating a map means iterating its `entrySet()`, which gives access to both key and value together:

```java
for (Map.Entry<String, Integer> entry : ages.entrySet()) {
    System.out.println(entry.getKey() + " -> " + entry.getValue());
}
```

## HashMap

`HashMap` is the default implementation: O(1) average-case `get`/`put`/`remove`, backed by an array of buckets indexed by each key's `hashCode()`. Like `HashSet` (which is literally backed by a `HashMap` internally), it makes **no guarantee about iteration order** — and for the exact same reason, keys must have a correct, consistent `equals()`/`hashCode()` pair (Day 17) or lookups silently fail to find entries that "should" match.

## LinkedHashMap and TreeMap

- **`LinkedHashMap`** — same O(1) average performance as `HashMap`, but iterates in insertion order (or optionally access order, useful for building an LRU cache).
- **`TreeMap`** — backed by a red-black tree, keeps keys in sorted order at all times, O(log n) operations instead of O(1). Like `TreeSet`, it implements `NavigableMap`, adding range-style queries: `firstKey()`, `lastKey()`, `higherKey(k)`, `ceilingKey(k)`.

```java
Map<String, Integer> sortedByKey = new TreeMap<>();
sortedByKey.put("banana", 3);
sortedByKey.put("apple", 5);
sortedByKey.put("cherry", 1);
System.out.println(sortedByKey); // {apple=5, banana=3, cherry=1} -- always sorted by key
```

## Useful default methods

Modern `Map` has several convenience methods that avoid manual null-checking boilerplate:

- **`getOrDefault(key, fallback)`** — returns the value for `key`, or `fallback` if absent, without a separate `containsKey` check.
- **`putIfAbsent(key, value)`** — only inserts if the key isn't already present; leaves an existing mapping untouched.
- **`computeIfAbsent(key, function)`** — if the key is absent, computes a value from the function, stores it, and returns it; a common pattern for building a `Map<K, List<V>>` without checking for `null` first.
- **`merge(key, value, function)`** — combines a new value with any existing one using the given function; classic use case is a word-frequency counter: `counts.merge(word, 1, Integer::sum)`.

```java
Map<String, Integer> wordCounts = new HashMap<>();
for (String word : new String[]{"a", "b", "a", "c", "a"}) {
    wordCounts.merge(word, 1, Integer::sum); // increments the count, starting from 0
}
System.out.println(wordCounts); // {a=3, b=1, c=1}
```

## Choosing between them

- **No ordering needed, want the fastest option** → `HashMap`.
- **Need iteration order to match insertion order** → `LinkedHashMap`.
- **Need keys kept sorted, or range queries** → `TreeMap`.
