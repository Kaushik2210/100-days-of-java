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

## LinkedList

`LinkedList` is backed by a doubly-linked list of nodes instead of an array. Adding or removing at either end (or once you already have a reference to the right spot) is O(1), because it's just relinking a couple of node pointers — no shifting. But `get(index)` is O(n): to reach index 5, it has to walk the chain from one end, node by node.

```java
List<String> playlist = new LinkedList<>();
playlist.add("Intro");
playlist.add("Track 1");
playlist.add(0, "Cold Open"); // cheap insert at the front -- O(1) for a LinkedList, O(n) for an ArrayList
System.out.println(playlist);
```

`LinkedList` also implements `Deque`, so it doubles as a double-ended queue with `addFirst`, `addLast`, `removeFirst`, `removeLast` — useful when you're using the list as a stack or queue rather than for indexed access.

## Choosing between them

- **Frequent access by index, or mostly appending to the end** → `ArrayList`. It's more cache-friendly and lower-overhead per element, and covers the vast majority of real-world list usage.
- **Frequent insertion/removal at the front or in the middle, or using the list as a queue/stack/deque** → `LinkedList`.

When in doubt, start with `ArrayList` — it's the right default unless profiling shows the insertion pattern actually needs `LinkedList`'s strengths.
