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

## Generic methods

A single method can introduce its own type parameter, independent of whether its class is generic at all — declare it in angle brackets right before the return type.

```java
class ListUtils {
    static <T> T firstOrNull(List<T> list) { // <T> declares the type parameter for this method
        return list.isEmpty() ? null : list.get(0);
    }
}
```

```java
String first = ListUtils.firstOrNull(List.of("a", "b")); // T is inferred as String
```

## Bounded type parameters

`<T extends Number>` restricts `T` to `Number` or one of its subclasses, which then lets the method call `Number`'s methods on values of type `T` — something a plain, unbounded `T` wouldn't allow, since the compiler otherwise only knows `T` is *some* `Object`.

```java
static double sum(List<? extends Number> numbers) {
    double total = 0;
    for (Number n : numbers) {
        total += n.doubleValue(); // legal because Number is guaranteed, thanks to the bound
    }
    return total;
}
```

## Wildcards: ? extends and ? super

A wildcard (`?`) is used at the call site, not in a class/method declaration, to describe a range of acceptable types for a parameter you're only reading from or only writing to:

- **`? extends T`** ("upper bounded") — accepts `T` or any subtype. Use it when you only need to *read* elements out (a "producer"), since the compiler won't let you add anything (it doesn't know the exact subtype).
- **`? super T`** ("lower bounded") — accepts `T` or any supertype. Use it when you only need to *write* elements in (a "consumer"), since any supertype's list can safely hold a `T`.

```java
static void printAll(List<? extends Number> list) { // producer -- reading only
    for (Number n : list) {
        System.out.println(n);
    }
}

static void addIntegers(List<? super Integer> list) { // consumer -- writing only
    list.add(1);
    list.add(2);
}
```

This is often summarized as **PECS**: *Producer Extends, Consumer Super* — a mnemonic for which wildcard to reach for based on whether the parameter is read from or written to.
