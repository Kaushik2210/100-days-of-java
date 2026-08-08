public class ExceptionsDemo {

    public static void main(String[] args) {
        try {
            int result = 10 / 0; // throws ArithmeticException
            System.out.println(result); // never reached
        } catch (ArithmeticException e) {
            System.out.println("Can't divide by zero: " + e.getMessage());
        }
    }
}
