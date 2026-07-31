public class AbstractionDemo {

    public static void main(String[] args) {
        Shape circle = new Circle(2.0);
        circle.describe();

        Shape square = new Square(3.0);
        square.describe();

        Shape triangle = new Triangle(4.0, 5.0);
        triangle.describe();
        System.out.println(triangle.name + " has " + ((Triangle) triangle).sides() + " sides");
    }
}

abstract class Shape {
    String name; // shared field

    Shape(String name) { // constructor -- only ever called via super(...)
        this.name = name;
    }

    abstract double area(); // no body -- subclasses must implement this

    void describe() { // concrete method, shared by all shapes
        System.out.println(name + " has area " + area());
    }
}

class Circle extends Shape {
    double radius;

    Circle(double radius) {
        super("Circle");
        this.radius = radius;
    }

    @Override
    double area() {
        return Math.PI * radius * radius;
    }
}

class Square extends Shape {
    double side;

    Square(double side) {
        super("Square");
        this.side = side;
    }

    @Override
    double area() {
        return side * side;
    }
}

// still abstract: doesn't implement area(), only adds a new abstract method
abstract class Polygon extends Shape {
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
