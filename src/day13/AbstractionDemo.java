public class AbstractionDemo {

    public static void main(String[] args) {
        Shape circle = new Circle(2.0);
        circle.describe();

        Shape square = new Square(3.0);
        square.describe();

        Shape triangle = new Triangle(4.0, 5.0);
        triangle.describe();
        System.out.println(triangle.name + " has " + ((Triangle) triangle).sides() + " sides");

        Payment[] payments = { new CardPayment(49.99), new UpiPayment(199.00) };
        for (Payment p : payments) {
            p.process(); // each subclass fills in its own transactionFee()
        }
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

// a second hierarchy showing a more real-world use of abstraction:
// every payment method shares the same processing steps, but calculates
// its own fee differently.
abstract class Payment {
    double amount;

    Payment(double amount) {
        this.amount = amount;
    }

    abstract double transactionFee();

    void process() { // shared workflow, reused by every payment type
        double total = amount + transactionFee();
        System.out.printf("Charging %.2f (fee: %.2f, total: %.2f)%n", amount, transactionFee(), total);
    }
}

class CardPayment extends Payment {
    CardPayment(double amount) {
        super(amount);
    }

    @Override
    double transactionFee() {
        return amount * 0.02; // 2% card fee
    }
}

class UpiPayment extends Payment {
    UpiPayment(double amount) {
        super(amount);
    }

    @Override
    double transactionFee() {
        return 0.0; // no fee for UPI
    }
}
