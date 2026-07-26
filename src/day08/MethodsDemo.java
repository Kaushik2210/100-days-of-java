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
}
