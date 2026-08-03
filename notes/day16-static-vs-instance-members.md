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

## Static methods

A `static` method belongs to the class the same way, and is called without ever creating an instance: `Math.max(3, 5)` never constructs a `Math` object. Static methods are the natural home for utility logic that doesn't depend on any particular object's state.

```java
class MathUtils {
    static int square(int n) {
        return n * n;
    }
}
```

```java
int result = MathUtils.square(6); // no MathUtils object needed
```

## The core rule: static code can't see instance state directly

A static method has no `this` — it isn't running "on" any particular object, so it cannot directly reference instance fields or call instance methods. Trying to do so is a compile error.

```java
class Counter {
    static int totalCreated;
    int count;

    static void badMethod() {
        count++; // compile error: cannot make a static reference to a non-static field
    }
}
```

An instance method, on the other hand, can freely use both instance members and static members, because it always runs in the context of a specific object *and* has access to the shared class-level state.

## Static initializer blocks

A `static { ... }` block runs exactly once, when the class is first loaded — before any object of that class is created and before `main` even starts if the class is the one being run. It's used to set up static state that's too complex for a single field initializer.

```java
class AppConfig {
    static final String VERSION;

    static {
        VERSION = "1.0." + (int) (Math.random() * 100); // some one-time computed value
        System.out.println("AppConfig loaded, version " + VERSION);
    }
}
```

## When to use static

Reach for `static` for: constants shared across all instances (`static final`), utility/helper methods that don't need object state (`Math.max`, `Collections.sort`), counters or registries tracking something about the whole class, and factory methods. Everything else — anything that describes one particular object's data — belongs as an instance member.
