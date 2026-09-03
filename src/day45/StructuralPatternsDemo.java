public class StructuralPatternsDemo {

    public static void main(String[] args) {
        Shape shape = new RectangleAdapter();
        shape.draw(10, 10, 50, 30); // caller only ever sees the Shape interface

        Coffee order = new SugarDecorator(new MilkDecorator(new SimpleCoffee())); // stack decorators freely
        System.out.println(order.description() + " = $" + order.cost());

        Database db = new LoggingProxy(); // caller only sees the Database interface
        db.query("SELECT * FROM users");
    }
}

interface Coffee {
    double cost();
    String description();
}

class SimpleCoffee implements Coffee {
    public double cost() { return 2.0; }
    public String description() { return "Coffee"; }
}

abstract class CoffeeDecorator implements Coffee {
    protected final Coffee wrapped;

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
        System.out.println("LOG: about to run a query");
        real.query(sql);
        System.out.println("LOG: query finished");
    }
}

class LegacyRectangle {
    void drawWithCoordinates(int x1, int y1, int x2, int y2) {
        System.out.println("Drawing rectangle from (" + x1 + "," + y1 + ") to (" + x2 + "," + y2 + ")");
    }
}

interface Shape {
    void draw(int x, int y, int width, int height);
}

class RectangleAdapter implements Shape {
    private final LegacyRectangle legacyRectangle = new LegacyRectangle();

    @Override
    public void draw(int x, int y, int width, int height) {
        legacyRectangle.drawWithCoordinates(x, y, x + width, y + height);
    }
}
