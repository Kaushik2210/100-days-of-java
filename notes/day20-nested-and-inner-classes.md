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
