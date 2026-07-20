public class OperatorsDemo {
    public static void main(String[] args) {
        int a = 17, b = 5;

        // Arithmetic operators
        System.out.println("a + b = " + (a + b));
        System.out.println("a - b = " + (a - b));
        System.out.println("a * b = " + (a * b));
        System.out.println("a / b = " + (a / b) + " (integer division truncates)");
        System.out.println("a % b = " + (a % b));
        System.out.println("17.0 / 5 = " + (17.0 / b) + " (decimal, since one operand is a double)");

        // Relational and logical operators
        int age = 20;
        boolean isCitizen = true;
        boolean isAdult = age >= 18;
        boolean canVote = isAdult && isCitizen;
        System.out.println("\nisAdult = " + isAdult);
        System.out.println("canVote = " + canVote);

        // Short-circuit evaluation avoids the unsafe array access below
        int[] array = {10, 20, 30};
        int index = 5;
        if (index < array.length && array[index] != 0) {
            System.out.println("Found value at index " + index);
        } else {
            System.out.println("\nShort-circuit prevented an out-of-bounds access at index " + index);
        }

        // Compound assignment
        int score = 10;
        score += 5;
        score -= 3;
        score *= 2;
        score /= 4;
        System.out.println("\nFinal score after compound assignments: " + score);

        // Increment/decrement
        int x = 5;
        int y = x++; // post-increment: y takes the old value
        int z = ++x; // pre-increment: x is incremented first
        System.out.println("x=" + x + ", y=" + y + ", z=" + z);

        // Operator precedence
        int result = 2 + 3 * 4;
        int forced = (2 + 3) * 4;
        System.out.println("\n2 + 3 * 4 = " + result + " (multiplication first)");
        System.out.println("(2 + 3) * 4 = " + forced + " (parentheses override precedence)");
    }
}
