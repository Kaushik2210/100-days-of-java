public class AbstractionDemo {

    public static void main(String[] args) {
        Shape circle = new Circle(2.0);
        circle.describe();

        Shape square = new Square(3.0);
        square.describe();
    }
}

abstract class Shape {
    abstract double area(); // no body -- subclasses must implement this

    void describe() { // concrete method, shared by all shapes
        System.out.println("This shape has area " + area());
    }
}

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

class Square extends Shape {
    double side;

    Square(double side) {
        this.side = side;
    }

    @Override
    double area() {
        return side * side;
    }
}
