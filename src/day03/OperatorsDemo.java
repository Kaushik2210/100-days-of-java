public class OperatorsDemo {
    public static void main(String[] args) {
        // Arithmetic operators: integer division truncates
        System.out.println("=== Arithmetic ===");
        System.out.println("7 / 2 = " + (7 / 2));
        System.out.println("7.0 / 2 = " + (7.0 / 2));
        System.out.println("-7 % 2 = " + (-7 % 2));
        System.out.println("7 % -2 = " + (7 % -2));

        // Compound assignment performs an implicit narrowing cast
        System.out.println("\n=== Compound assignment ===");
        byte b = 10;
        b += 5;
        System.out.println("byte b += 5 -> " + b);
        // The line below would NOT compile if uncommented:
        // b = b + 5; // int cannot be assigned to byte without an explicit cast

        // Increment and decrement
        System.out.println("\n=== Increment / decrement ===");
        int x = 5;
        int a = ++x;
        System.out.println("x = 5; a = ++x -> a=" + a + ", x=" + x);

        int y = 5;
        int c = y++;
        System.out.println("y = 5; c = y++ -> c=" + c + ", y=" + y);

        // Relational and logical operators, short-circuit evaluation
        System.out.println("\n=== Logical short-circuit ===");
        String s = null;
        if (s != null && s.length() > 0) {
            System.out.println("non-empty");
        } else {
            System.out.println("s is null, s.length() was never called");
        }

        // Ternary operator
        System.out.println("\n=== Ternary ===");
        int p = 7, q = 3;
        int max = (p > q) ? p : q;
        System.out.println("max(7, 3) = " + max);

        // Operator precedence
        System.out.println("\n=== Precedence ===");
        int result = 2 + 3 * 4;
        int clearer = 2 + (3 * 4);
        System.out.println("2 + 3 * 4 = " + result);
        System.out.println("2 + (3 * 4) = " + clearer);
    }
}
