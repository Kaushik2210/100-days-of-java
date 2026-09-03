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
