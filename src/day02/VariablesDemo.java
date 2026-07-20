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

        // Wrapper classes and autoboxing/unboxing
        int primitive = 42;
        Integer boxed = primitive;         // autoboxing
        int backToPrimitive = boxed;       // unboxing
        System.out.println("\nAutoboxed int -> Integer: " + boxed);
        System.out.println("Unboxed Integer -> int: " + backToPrimitive);
        System.out.println("Integer.parseInt(\"123\") = " + Integer.parseInt("123"));
        System.out.println("Integer.MAX_VALUE = " + Integer.MAX_VALUE);

        // The == vs equals() trap with boxed values outside the -128..127 cache range
        Integer boxedA = 200;
        Integer boxedB = 200;
        System.out.println("\nboxedA == boxedB (200, 200): " + (boxedA == boxedB) + " (reference comparison)");
        System.out.println("boxedA.equals(boxedB): " + boxedA.equals(boxedB) + " (value comparison)");

        // var type inference
        var count = 10;
        var name = "Kaushik";
        var pricePerItem = 19.99;
        System.out.println("\nvar count = " + count + " (inferred as int)");
        System.out.println("var name = " + name + " (inferred as String)");
        System.out.println("var pricePerItem = " + pricePerItem + " (inferred as double)");

        // Characters are numbers underneath
        char ch = 'A';
        int code = ch;
        char next = (char) (ch + 1);
        System.out.println("\n'A' as int: " + code);
        System.out.println("'A' + 1, cast back to char: " + next);

        // Readable numeric literals and constants
        int million = 1_000_000;
        final double TAX_RATE = 0.08;
        System.out.println("\n1_000_000 = " + million);
        System.out.println("TAX_RATE (final, cannot be reassigned) = " + TAX_RATE);

        // Primitive narrowing casts never throw -- they silently overflow instead
        long tooBig = 5_000_000_000L;
        int overflowed = (int) tooBig;
        System.out.println("\n(int) 5_000_000_000L = " + overflowed + " (silent overflow, no exception)");

        // NumberFormatException: thrown when parsing text that isn't a valid number
        try {
            int value = Integer.parseInt("not a number");
            System.out.println("Parsed: " + value);
        } catch (NumberFormatException e) {
            System.out.println("Caught NumberFormatException: " + e.getMessage());
        }

        // ClassCastException: thrown when a reference-type cast is invalid at runtime
        Object obj = "a String, not a number";
        try {
            Integer number = (Integer) obj;
            System.out.println("Cast succeeded: " + number);
        } catch (ClassCastException e) {
            System.out.println("Caught ClassCastException: " + e.getMessage());
        }
    }
}
