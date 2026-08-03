# Day 16: Static vs Instance Members

Every field and method in a class is either **instance-level** (belongs to each individual object) or **static** (belongs to the class itself, shared by every object). Understanding which one you're using — and when to reach for each — is central to how Java models shared vs. per-object state.

## Instance members

By default, fields and methods are instance members: each object gets its own copy of the fields, and methods operate on that specific object's data.

```java
class Counter {
    int count; // each Counter object has its own count

    void increment() {
        count++;
    }
}
```

```java
Counter a = new Counter();
Counter b = new Counter();
a.increment();
a.increment();
b.increment();
System.out.println(a.count); // 2
System.out.println(b.count); // 1 -- completely independent of a
```

## Static members

Add the `static` keyword and the field belongs to the *class*, not to any one object. There is exactly one copy, shared across every instance — and it exists even before any object has been created.

```java
class Counter {
    static int totalCreated; // one copy, shared by every Counter
    int id;

    Counter() {
        totalCreated++; // every new Counter bumps the shared count
        id = totalCreated;
    }
}
```

```java
Counter a = new Counter(); // totalCreated becomes 1, a.id = 1
Counter b = new Counter(); // totalCreated becomes 2, b.id = 2
System.out.println(Counter.totalCreated); // 2 -- read via the class name, not an instance
```

Static members should be accessed through the class name (`Counter.totalCreated`), not through an instance reference, even though Java technically allows the latter — accessing it via an instance is misleading, since it makes shared state look like it belongs to just that object.
