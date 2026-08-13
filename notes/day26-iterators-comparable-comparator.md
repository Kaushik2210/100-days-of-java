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

## Comparable: a type's natural ordering

A class implements `Comparable<T>` to say "instances of me have one obvious, built-in way to be ordered." It declares a single method, `compareTo(T other)`, returning negative if `this` comes before `other`, positive if after, and zero if they're considered equal for ordering purposes.

```java
class Person implements Comparable<Person> {
    String name;
    int age;

    Person(String name, int age) {
        this.name = name;
        this.age = age;
    }

    @Override
    public int compareTo(Person other) {
        return Integer.compare(this.age, other.age); // natural order: youngest first
    }
}
```

Once a class implements `Comparable`, `Collections.sort(list)` and `Collections.max`/`min` work on it directly, with no extra arguments — and it can be used as a key in a `TreeMap`/`TreeSet` without supplying a separate comparator.

```java
List<Person> people = new ArrayList<>(List.of(new Person("Kiran", 25), new Person("Asha", 30)));
Collections.sort(people); // uses compareTo() -- sorts by age ascending
```

## Comparator: an alternate or external ordering

Sometimes you need to sort by something other than the natural order, or sort a class you don't control and can't add `Comparable` to. A `Comparator<T>` is a separate object describing one specific ordering, passed in at sort time instead of baked into the class.

```java
Comparator<Person> byName = (a, b) -> a.name.compareTo(b.name);
people.sort(byName); // sorts by name instead of age, without touching Person at all
```

`Comparator` has handy static/default methods for building these declaratively instead of by hand:

```java
Comparator<Person> byAgeThenName = Comparator
    .comparingInt((Person p) -> p.age)
    .thenComparing(p -> p.name); // tiebreaker when ages are equal

people.sort(byAgeThenName.reversed()); // oldest first
```

**Rule of thumb**: implement `Comparable` when a type has one clear natural order it should always sort by; reach for `Comparator` for every other ordering, or when you can't modify the class.
