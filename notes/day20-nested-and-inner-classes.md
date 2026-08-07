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
