public class RecursionDemo {

    public static void main(String[] args) {
        System.out.println("factorial(4) = " + factorial(4));

        System.out.println();
        System.out.println("Tracing factorial(4):");
        factorialTraced(4, 0);

        System.out.println();
        callCount = 0;
        int fibResult = fibonacci(10);
        System.out.println("fibonacci(10) = " + fibResult + ", using " + callCount + " calls");
        System.out.println("(naive O(2^n) recursion recomputes the same smaller values repeatedly)");

        System.out.println();
        System.out.println("factorialTailRecursive(4, 1) = " + factorialTailRecursive(4, 1));
    }

    static int callCount = 0;

    static int fibonacci(int n) {
        callCount++;
        if (n <= 1) return n;                       // base case
        return fibonacci(n - 1) + fibonacci(n - 2);  // two recursive calls -- branches the tree
    }

    static int factorialTailRecursive(int n, int accumulator) {
        if (n <= 1) return accumulator;
        return factorialTailRecursive(n - 1, n * accumulator); // nothing left to do after this returns
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
