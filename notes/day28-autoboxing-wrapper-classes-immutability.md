# Day 28: Autoboxing, Wrapper Classes & Immutability

Every primitive type (`int`, `double`, `boolean`, ...) has a corresponding **wrapper class** (`Integer`, `Double`, `Boolean`, ...) that represents the same value as a real object. Wrapper classes exist because primitives can't be used where an `Object` is required — generics, collections (`List<Integer>`, never `List<int>`), and anything expecting a reference type.

## Autoboxing and unboxing

Since Java 5, the compiler automatically converts between a primitive and its wrapper wherever needed — this is **autoboxing** (primitive → wrapper) and **unboxing** (wrapper → primitive). You rarely need to call `Integer.valueOf(x)` or `.intValue()` yourself.

```java
Integer boxed = 42;       // autoboxing: int -> Integer, compiler inserts Integer.valueOf(42)
int unboxed = boxed;      // unboxing: Integer -> int, compiler inserts boxed.intValue()

List<Integer> numbers = new ArrayList<>();
numbers.add(5);           // autoboxed: int 5 -> Integer.valueOf(5)
int first = numbers.get(0); // unboxed back to int
```

## The Integer cache and == pitfalls

For performance, `Integer.valueOf(int)` caches and reuses instances for values from **-128 to 127**. Autoboxing a value in that range reuses a cached object; outside it, a new `Integer` is allocated every time. This makes `==` (reference comparison) behave inconsistently on boxed values, which is exactly why it should be avoided for them.

```java
Integer a = 100;
Integer b = 100;
System.out.println(a == b); // true -- both point to the same cached Integer(100)

Integer c = 200;
Integer d = 200;
System.out.println(c == d); // false -- outside the cache range, two distinct objects

System.out.println(c.equals(d)); // true -- equals() compares values, always correct
```

**Rule of thumb**: always compare wrapper objects with `.equals()`, never `==` — the cache makes `==` "accidentally" work for small values and fail for larger ones, which is a trap, not a feature to rely on.
