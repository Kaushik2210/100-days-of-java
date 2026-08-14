# Day 27: Generics

Generics let a class, interface, or method be written once and work with any type, while still catching type mismatches at compile time instead of at runtime. `List<String>` and `List<Integer>` are the same `List` class, parameterized differently — that parameterization is what generics provide.

## Why generics: the problem they solve

Before generics (pre-Java 5), collections held plain `Object`, and pulling an element back out required an explicit, unchecked cast:

```java
List rawList = new ArrayList(); // raw type -- no compile-time type safety
rawList.add("hello");
rawList.add(42); // no error -- rawList accepts anything
String s = (String) rawList.get(1); // compiles, but throws ClassCastException at runtime
```

With a generic type parameter, the compiler enforces the type at every call site, and the cast becomes unnecessary:

```java
List<String> names = new ArrayList<>();
names.add("hello");
names.add(42); // compile error -- caught immediately, not at runtime
String s = names.get(0); // no cast needed
```

## Writing your own generic class

Declare a type parameter (conventionally a single uppercase letter — `T` for "Type", `E` for "Element", `K`/`V` for "Key"/"Value") in angle brackets after the class name, then use it like any other type inside the class.

```java
class Box<T> {
    private T content;

    void set(T content) {
        this.content = content;
    }

    T get() {
        return content;
    }
}
```

```java
Box<String> stringBox = new Box<>();
stringBox.set("hello");
String value = stringBox.get(); // already String -- no cast needed

Box<Integer> intBox = new Box<>();
intBox.set(42);
```

The same `Box` class works for any type, and the compiler enforces that a `Box<String>` never accidentally holds an `Integer`.
