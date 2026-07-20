public class VariablesDemo {
    public static void main(String[] args) {
        // The eight primitive types
        byte smallNumber = 100;
        short mediumNumber = 30000;
        int wholeNumber = 42;
        long bigNumber = 10000000000L;
        float singlePrecision = 3.14f;
        double doublePrecision = 3.14159;
        char letter = 'A';
        boolean isActive = true;

        System.out.println("byte: " + smallNumber);
        System.out.println("short: " + mediumNumber);
        System.out.println("int: " + wholeNumber);
        System.out.println("long: " + bigNumber);
        System.out.println("float: " + singlePrecision);
        System.out.println("double: " + doublePrecision);
        System.out.println("char: " + letter);
        System.out.println("boolean: " + isActive);

        // Widening: happens automatically, no data loss possible
        int intValue = 42;
        double widened = intValue;
        System.out.println("\nWidened int -> double: " + widened);

        // Narrowing: must be explicit, and it truncates rather than rounds
        double price = 19.99;
        int narrowed = (int) price;
        System.out.println("Narrowed double -> int: " + narrowed + " (truncated, not rounded)");

        // Floating point comparison pitfall
        double a = 0.1;
        double b = 0.2;
        System.out.println("\n0.1 + 0.2 == 0.3 ? " + (a + b == 0.3));
        System.out.println("Actual value of 0.1 + 0.2: " + (a + b));
    }
}
