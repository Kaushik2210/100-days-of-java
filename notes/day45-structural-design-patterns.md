# Day 45: Design Patterns — Structural (Adapter, Decorator, Proxy)

Day 44 covered patterns for *creating* objects. **Structural** patterns are about *composing* objects and classes into larger structures — wrapping one type to present a different interface, adding behavior without touching the original class, or standing in for another object.

## Adapter: making incompatible interfaces work together

An Adapter wraps an existing class behind a new interface that client code actually expects, without modifying the original class at all. It's the classic "translate between two APIs that don't naturally agree" pattern.

```java
// An existing, third-party-style class with an interface we can't change
class LegacyRectangle {
    void drawWithCoordinates(int x1, int y1, int x2, int y2) {
        System.out.println("Drawing rectangle from (" + x1 + "," + y1 + ") to (" + x2 + "," + y2 + ")");
    }
}

// The interface our code actually wants to program against
interface Shape {
    void draw(int x, int y, int width, int height);
}

// Adapter: translates the Shape call into the shape LegacyRectangle expects
class RectangleAdapter implements Shape {
    private final LegacyRectangle legacyRectangle = new LegacyRectangle();

    @Override
    public void draw(int x, int y, int width, int height) {
        legacyRectangle.drawWithCoordinates(x, y, x + width, y + height); // translates the call
    }
}
```

```java
Shape shape = new RectangleAdapter();
shape.draw(10, 10, 50, 30); // caller only ever sees the Shape interface
```

Callers work entirely through `Shape`, never knowing (or caring) that a `LegacyRectangle` sits underneath — useful whenever integrating a third-party class or older code that can't be changed to match the interface your codebase now expects.

## Decorator: adding behavior by wrapping, not subclassing

A Decorator wraps an object of the same interface and adds behavior before/after delegating to it — letting behaviors be combined at runtime by nesting wrappers, instead of needing a new subclass for every combination (Day 11's inheritance doesn't scale well when features can mix freely: a plain coffee, coffee+milk, coffee+milk+sugar would each need their own subclass).

```java
interface Coffee {
    double cost();
    String description();
}

class SimpleCoffee implements Coffee {
    public double cost() { return 2.0; }
    public String description() { return "Coffee"; }
}

abstract class CoffeeDecorator implements Coffee {
    protected final Coffee wrapped; // wraps another Coffee -- could itself be another decorator

    CoffeeDecorator(Coffee wrapped) {
        this.wrapped = wrapped;
    }
}

class MilkDecorator extends CoffeeDecorator {
    MilkDecorator(Coffee wrapped) { super(wrapped); }
    public double cost() { return wrapped.cost() + 0.5; }
    public String description() { return wrapped.description() + " + Milk"; }
}

class SugarDecorator extends CoffeeDecorator {
    SugarDecorator(Coffee wrapped) { super(wrapped); }
    public double cost() { return wrapped.cost() + 0.2; }
    public String description() { return wrapped.description() + " + Sugar"; }
}
```

```java
Coffee order = new SugarDecorator(new MilkDecorator(new SimpleCoffee())); // stack decorators freely
System.out.println(order.description() + " = $" + order.cost()); // Coffee + Milk + Sugar = $2.7
```

Each decorator only knows how to add its own contribution and delegate the rest — any combination of add-ons is possible just by nesting wrappers differently, with zero new classes needed per combination. Java's own `BufferedReader(new FileReader(...))` from Day 34 is a real-world Decorator in the standard library.

## Proxy: standing in for another object

A Proxy implements the same interface as a "real" object and controls access to it — adding a check, a cache, lazy initialization, or logging around calls, without the caller knowing it isn't talking to the real thing directly.

```java
interface Database {
    void query(String sql);
}

class RealDatabase implements Database {
    public void query(String sql) {
        System.out.println("Executing: " + sql);
    }
}

class LoggingProxy implements Database {
    private final RealDatabase real = new RealDatabase();

    public void query(String sql) {
        System.out.println("LOG: about to run a query"); // extra behavior before delegating
        real.query(sql);
        System.out.println("LOG: query finished");
    }
}
```

```java
Database db = new LoggingProxy(); // caller only sees the Database interface
db.query("SELECT * FROM users");
```

The difference from Decorator is intent, not structure: a Decorator adds new *behavior/features*; a Proxy controls *access* to the underlying object (permissions, caching, remote calls) while keeping the exact same behavior from the caller's point of view.
