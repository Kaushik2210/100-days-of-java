# Day 29: Lambda Expressions & Functional Interfaces

A **functional interface** is any interface with exactly one abstract method (default and static methods don't count). Day 14 already met one shape of this idea; what Java 8 added is a compact syntax — the **lambda expression** — for writing an implementation of a functional interface inline, without the ceremony of an anonymous class.

## Functional interfaces

```java
@FunctionalInterface // optional, but documents intent and lets the compiler enforce the rule
interface Greeter {
    String greet(String name); // exactly one abstract method
}
```

Any interface shaped like this — one abstract method, however many default/static methods — is "functional," and can be implemented with a lambda instead of a class.

## Lambda syntax

A lambda is `(parameters) -> body`. The compiler infers the parameter types from the functional interface's method signature, so they're almost never written explicitly.

```java
Greeter formal = (name) -> "Good day, " + name + ".";
Greeter casual = name -> "Hey " + name + "!"; // parentheses optional for a single parameter
Greeter multiLine = (name) -> {
    String trimmed = name.trim();
    return "Hello, " + trimmed;
}; // braces + explicit return needed once the body is more than one expression

System.out.println(formal.greet("Dr. Rao"));
System.out.println(casual.greet("Kiran"));
```

Compare this to Day 20's anonymous class for the same interface — a lambda is the same mechanism (an object implementing the interface), just without repeating the interface name, the method name, or the boilerplate braces:

```java
Greeter viaAnonymousClass = new Greeter() {
    @Override
    public String greet(String name) {
        return "Hey " + name + "!";
    }
};
```

Lambdas are most commonly passed directly as arguments, replacing what used to require an anonymous class — a `Comparator` for `List.sort()`, a `Runnable` for a thread, an event handler.

## Built-in functional interfaces (java.util.function)

Rather than declaring a new functional interface for every use case, `java.util.function` provides a standard set covering the common shapes:

| Interface | Method | Signature | Purpose |
|---|---|---|---|
| `Function<T, R>` | `apply(T) -> R` | takes one, returns one | transform a value |
| `Predicate<T>` | `test(T) -> boolean` | takes one, returns boolean | a yes/no check |
| `Consumer<T>` | `accept(T) -> void` | takes one, returns nothing | do something with a value |
| `Supplier<T>` | `get() -> T` | takes nothing, returns one | produce a value on demand |
| `BiFunction<T, U, R>` | `apply(T, U) -> R` | takes two, returns one | combine two values |

```java
Function<String, Integer> length = s -> s.length();
Predicate<Integer> isEven = n -> n % 2 == 0;
Consumer<String> printer = s -> System.out.println("Got: " + s);
Supplier<String> idGenerator = () -> "id-" + System.nanoTime();

System.out.println(length.apply("hello"));      // 5
System.out.println(isEven.test(4));              // true
printer.accept("done");                          // Got: done
System.out.println(idGenerator.get());           // id-... (a new value each call)
```

These interfaces are what `Stream` (Day 30) is built entirely on top of.

## Method references

When a lambda does nothing but call one existing method, a **method reference** (`::`) says the same thing more directly, without an explicit parameter list.

```java
Function<String, Integer> length1 = s -> s.length(); // lambda
Function<String, Integer> length2 = String::length;   // equivalent method reference

Consumer<String> printer1 = s -> System.out.println(s);
Consumer<String> printer2 = System.out::println; // equivalent
```

There are four kinds of method reference: `ClassName::staticMethod`, `instance::instanceMethod`, `ClassName::instanceMethod` (the instance becomes the first parameter), and `ClassName::new` (a constructor reference, matching a `Supplier`/`Function`-shaped interface).
