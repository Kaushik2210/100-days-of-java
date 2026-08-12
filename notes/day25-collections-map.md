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
