public class MethodsDemo {

    public static void main(String[] args) {
        // calling a method that returns a value
        int sum = add(3, 5);
        System.out.println("add(3, 5) = " + sum);

        // calling a void method for its side effect
        greet("Kaushik");

        // early return based on a condition
        System.out.println("isValidAge(25) = " + isValidAge(25));
        System.out.println("isValidAge(-3) = " + isValidAge(-3));
        System.out.println("isValidAge(200) = " + isValidAge(200));

        // pass-by-value demonstration
        int x = 5;
        System.out.println("x before increment(x): " + x);
        increment(x);
        System.out.println("x after increment(x): " + x + " (unchanged)");

        // method overloading: same name, different parameter lists
        System.out.println("multiply(3, 4) = " + multiply(3, 4));
        System.out.println("multiply(2.5, 4.0) = " + multiply(2.5, 4.0));
        System.out.println("multiply(2, 3, 4) = " + multiply(2, 3, 4));

        // an int argument widens to match the double overload here,
        // since there's no exact multiply(int, double) overload
        System.out.println("multiply(5, 2.0) = " + multiply(5, 2.0));

        // varargs: same method, any number of arguments
        System.out.println("sum() = " + sum());
        System.out.println("sum(5) = " + sum(5));
        System.out.println("sum(1, 2, 3, 4) = " + sum(1, 2, 3, 4));

        // an existing array can be passed straight in too
        int[] values = {10, 20, 30};
        System.out.println("sum(values array) = " + sum(values));
    }

    public static int add(int a, int b) {
        return a + b;
    }

    public static void greet(String name) {
        System.out.println("Hello, " + name + "!");
    }

    public static boolean isValidAge(int age) {
        if (age < 0) {
            return false;
        }
        return age <= 150;
    }

    public static void increment(int n) {
        n = n + 1;
        // this only changes the local copy of n, not the caller's variable
    }

    public static int multiply(int a, int b) {
        return a * b;
    }

    public static double multiply(double a, double b) {
        return a * b;
    }

    public static int multiply(int a, int b, int c) {
        return a * b * c;
    }

    public static int sum(int... numbers) {
        int total = 0;
        for (int n : numbers) {
            total += n;
        }
        return total;
    }
}
