# Day 13: Abstraction — Abstract Classes

Abstraction means hiding implementation details and exposing only what a class needs to promise it can do. In Java, one of the main tools for this is the **abstract class** — a class that can't be instantiated directly and is meant to be extended.

## Declaring an abstract class

Use the `abstract` keyword on the class. An abstract class can mix two kinds of methods:

- **Abstract methods** — declared with no body, ending in a semicolon. Subclasses are forced to implement them.
- **Concrete methods** — regular methods with a full body, shared by every subclass.

```java
abstract class Shape {
    abstract double area(); // no body -- subclasses must implement this

    void describe() { // concrete method, shared by all shapes
        System.out.println("This shape has area " + area());
    }
}
```

## Why you can't instantiate one

`new Shape()` won't compile. An abstract class is intentionally incomplete — it has at least one method with no implementation, so there's nothing meaningful to run if you tried to call it directly. Java refuses to let you create an object of a type that doesn't fully know how to behave.

```java
Shape s = new Shape(); // compile error: Shape is abstract; cannot be instantiated
```

Instead, you extend it with a concrete subclass that fills in every abstract method:

```java
class Circle extends Shape {
    double radius;

    Circle(double radius) {
        this.radius = radius;
    }

    @Override
    double area() {
        return Math.PI * radius * radius;
    }
}
```

Now `new Circle(2.0)` works, because `Circle` supplies a real implementation for `area()`. Once every abstract method has a body, the class is "complete" and can be instantiated.

## Abstract classes can have constructors and fields

Even though you can never call `new` directly on an abstract class, it's still allowed to declare fields and a constructor. Subclasses call that constructor with `super(...)`, exactly like ordinary inheritance from Day 11 — the constructor just never runs on its own, only as part of building a subclass object.

```java
abstract class Shape {
    String name; // shared field

    Shape(String name) { // constructor -- only ever called via super(...)
        this.name = name;
    }

    abstract double area();

    void describe() {
        System.out.println(name + " has area " + area());
    }
}

class Circle extends Shape {
    double radius;

    Circle(double radius) {
        super("Circle"); // runs Shape's constructor
        this.radius = radius;
    }

    @Override
    double area() {
        return Math.PI * radius * radius;
    }
}
```

## A subclass of an abstract class can stay abstract

If a subclass doesn't implement every inherited abstract method, it must also be declared `abstract` — Java won't let a class be "half-complete" and still instantiable. Only when a class in the chain finally fills in all abstract methods does it become concrete.

```java
abstract class Polygon extends Shape {
    // doesn't implement area() -- still abstract, still can't be instantiated
    Polygon(String name) {
        super(name);
    }

    abstract int sides();
}

class Triangle extends Polygon {
    double base, height;

    Triangle(double base, double height) {
        super("Triangle");
        this.base = base;
        this.height = height;
    }

    @Override
    double area() {
        return 0.5 * base * height;
    }

    @Override
    int sides() {
        return 3;
    }
}
```

`Triangle` is finally concrete because it implements both `area()` (from `Shape`) and `sides()` (from `Polygon`).

Tomorrow's notes continue with more abstract class rules and how this compares to interfaces (Day 14).
