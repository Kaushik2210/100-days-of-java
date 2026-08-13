# Day 26: Iterators, Comparable & Comparator

Two separate concerns come up constantly when working with collections: walking through elements one at a time safely, and defining what "order" even means for a type. Java has a dedicated tool for each: `Iterator` for traversal, and `Comparable`/`Comparator` for ordering.

## The Iterator interface

Any class implementing `Iterable<E>` (every collection does) can produce an `Iterator<E>` via `iterator()`, which exposes three methods: `hasNext()`, `next()`, and `remove()`.

```java
List<String> names = new ArrayList<>(List.of("Asha", "Kiran", "Ravi"));
Iterator<String> it = names.iterator();
while (it.hasNext()) {
    String name = it.next();
    System.out.println(name);
}
```

The enhanced for-loop (`for (String name : names)`) is exactly this pattern under the hood — the compiler rewrites it into the `iterator()`/`hasNext()`/`next()` calls above. Java lets you write either form; the for-each is just more concise when you don't need `remove()`.

## Removing safely during iteration

Modifying a collection's structure (`add`/`remove`) directly while a for-each loop is iterating it throws `ConcurrentModificationException` — the collection detects it was changed out from under the iterator and refuses to continue with stale state.

```java
List<Integer> numbers = new ArrayList<>(List.of(1, 2, 3, 4, 5));
for (Integer n : numbers) {
    if (n % 2 == 0) {
        numbers.remove(n); // throws ConcurrentModificationException
    }
}
```

The fix is to remove through the iterator itself, which knows how to keep its internal state consistent:

```java
Iterator<Integer> it = numbers.iterator();
while (it.hasNext()) {
    if (it.next() % 2 == 0) {
        it.remove(); // safe -- the iterator updates its own bookkeeping
    }
}
```
