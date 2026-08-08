public class ExceptionsDemo {

    public static void main(String[] args) {
        try {
            int result = 10 / 0; // throws ArithmeticException
            System.out.println(result); // never reached
        } catch (ArithmeticException e) {
            System.out.println("Can't divide by zero: " + e.getMessage());
        }

        try {
            int[] numbers = { 1, 2, 3 };
            System.out.println(numbers[5]); // throws ArrayIndexOutOfBoundsException
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Bad index: " + e.getMessage());
        } catch (Exception e) { // broader catch-all, must come after the specific one
            System.out.println("Something else went wrong: " + e.getMessage());
        }

        try {
            System.out.println("Opening resource");
            throw new RuntimeException("boom");
        } catch (RuntimeException e) {
            System.out.println("Caught: " + e.getMessage());
        } finally {
            System.out.println("Closing resource"); // always runs
        }
    }
}
