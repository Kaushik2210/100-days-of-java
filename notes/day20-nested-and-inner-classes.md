# Day 20: Nested & Inner Classes

Java lets you declare a class inside another class. There are four flavors — static nested, (non-static) inner, local, and anonymous — and they differ mainly in whether they're tied to an instance of the enclosing class and how much of the surrounding scope they can see.

## Static nested classes

A `static` class declared inside another class. It behaves like a top-level class that just happens to be namespaced inside its enclosing class — it does **not** get a reference to any particular outer instance, and can be created without one.

```java
class Computer {
    String model;

    Computer(String model) {
        this.model = model;
    }

    static class Battery { // does not need a Computer instance to exist
        int capacityMah;

        Battery(int capacityMah) {
            this.capacityMah = capacityMah;
        }
    }
}
```

```java
Computer.Battery battery = new Computer.Battery(5000); // no Computer object involved
```

Use a static nested class when the nested type is logically grouped with the outer class (it belongs in that namespace) but doesn't need access to any specific outer object's state — e.g., a `Map.Entry`-style helper type, or a builder.

## (Non-static) inner classes

Drop the `static` keyword and the nested class becomes an **inner class**: every instance of it is implicitly tied to one specific instance of the outer class, and can freely read and write that outer instance's fields — even private ones.

```java
class Computer {
    String model;
    int fanSpeed;

    Computer(String model) {
        this.model = model;
    }

    class DiagnosticsReport { // tied to one specific Computer instance
        String summarize() {
            return model + " running fan at " + fanSpeed + " RPM"; // reads the outer instance's fields directly
        }
    }
}
```

Creating an inner class instance requires an outer instance to attach to — you construct it *through* that instance with `outer.new Inner()`:

```java
Computer laptop = new Computer("ThinkPad");
laptop.fanSpeed = 2200;

Computer.DiagnosticsReport report = laptop.new DiagnosticsReport();
System.out.println(report.summarize()); // ThinkPad running fan at 2200 RPM
```

Use an inner class when the nested type genuinely represents "a piece of this specific outer object" and needs direct access to its state — an iterator over a specific collection instance is the classic example (`ArrayList.Itr` is a real inner class in the JDK).

## Local classes

A class declared **inside a method body** — visible only within that method, and able to capture the method's local variables (as long as they're effectively final, i.e. never reassigned after being set).

```java
class ReportGenerator {
    String buildGreeting(String name) {
        class Greeter { // only exists inside this method
            String greet() {
                return "Hello, " + name + "!"; // captures the enclosing method's local variable
            }
        }
        return new Greeter().greet();
    }
}
```

Local classes are rare in modern code — lambdas and method references usually cover the same need more concisely — but they're useful when you need a small helper with multiple methods or mutable state, scoped tightly to one method.

## Anonymous classes

A class with no name, declared and instantiated in a single expression — typically to supply a one-off implementation of an interface or abstract class without formally naming a new type.

```java
interface Greeting {
    String message();
}

Greeting g = new Greeting() { // anonymous implementation, defined right where it's used
    @Override
    public String message() {
        return "Hi there!";
    }
};
System.out.println(g.message());
```

Like local classes, anonymous classes can capture effectively-final local variables from the enclosing scope. Since Java 8, a functional interface (one abstract method) is usually better expressed as a lambda instead — `Runnable r = () -> System.out.println("run");` is shorter than the equivalent anonymous class — but anonymous classes are still needed for interfaces/abstract classes with more than one method to implement, or when you need fields of your own inside the implementation.
