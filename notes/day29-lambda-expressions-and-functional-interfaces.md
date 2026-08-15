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
