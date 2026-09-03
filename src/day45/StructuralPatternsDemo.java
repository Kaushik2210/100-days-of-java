public class StructuralPatternsDemo {

    public static void main(String[] args) {
        Shape shape = new RectangleAdapter();
        shape.draw(10, 10, 50, 30); // caller only ever sees the Shape interface
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
