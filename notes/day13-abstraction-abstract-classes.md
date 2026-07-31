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

Tomorrow's notes continue with more abstract class rules: abstract classes can have constructors and fields, a subclass of an abstract class can itself stay abstract, and how this compares to interfaces (Day 14).
