public class RecursionDemo {

    public static void main(String[] args) {
        System.out.println("factorial(4) = " + factorial(4));

        System.out.println();
        System.out.println("Tracing factorial(4):");
        factorialTraced(4, 0);
    }

    static int factorial(int n) {
        if (n <= 1) return 1;        // base case -- stops the recursion
        return n * factorial(n - 1); // recursive case -- smaller problem, same shape
    }

    static int factorialTraced(int n, int depth) {
        System.out.println("  ".repeat(depth) + "factorial(" + n + ") called");
        if (n <= 1) {
            System.out.println("  ".repeat(depth) + "-> base case, returning 1");
            return 1;
        }
        int result = n * factorialTraced(n - 1, depth + 1);
        System.out.println("  ".repeat(depth) + "-> factorial(" + n + ") returns " + result);
        return result;
    }
}
