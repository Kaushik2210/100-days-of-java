public class Calculator {
    int add(int a, int b) {
        return a + b;
    }

    int divide(int a, int b) {
        if (b == 0) throw new ArithmeticException("Cannot divide by zero");
        return a / b;
    }
}
