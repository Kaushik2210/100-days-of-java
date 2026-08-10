# Day 23: Collections Framework — List (ArrayList, LinkedList)

Arrays (Day 6) have a fixed size decided at creation. The Collections Framework's `List` interface is Java's answer to a growable, shrinkable sequence: an ordered collection that allows duplicates and lets you insert, remove, and access elements by index.

## The List interface

`List<E>` is generic (Day 27 covers generics in depth) and declares the operations every list-like collection supports: `add`, `get`, `set`, `remove`, `size`, `contains`, iteration, and more. You almost always code against the interface type, and only pick a concrete implementation at construction time.

```java
List<String> names = new ArrayList<>(); // declared type is the interface, not the implementation
names.add("Asha");
names.add("Kiran");
names.add("Asha"); // duplicates are allowed
System.out.println(names.get(1)); // Kiran
System.out.println(names.size()); // 3
```

## ArrayList

`ArrayList` is backed by a resizable array under the hood. Reading any element by index is O(1) — it's a direct array access — but inserting or removing from the middle requires shifting every following element, making that O(n).

```java
List<String> queue = new ArrayList<>(List.of("a", "b", "c"));
queue.add(1, "x");       // insert at index 1 -- shifts b, c right
queue.remove("a");       // removes the first matching element -- shifts everything left
System.out.println(queue); // [x, b, c]
```

`ArrayList` is the default choice for most `List` needs: fast random access, and additions/removals mostly happen at the end (which is O(1) amortized, since the backing array only needs to resize occasionally).
