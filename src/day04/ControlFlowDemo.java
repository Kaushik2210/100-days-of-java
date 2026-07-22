public class ControlFlowDemo {
    public static void main(String[] args) {
        // Plain if
        System.out.println("=== Plain if ===");
        int age = 20;
        if (age >= 18) {
            System.out.println("adult");
        }

        // else-if grading chain
        System.out.println("\n=== else-if chain ===");
        int[] scores = {95, 82, 71, 40};
        for (int score : scores) {
            String grade;
            if (score >= 90) {
                grade = "A";
            } else if (score >= 80) {
                grade = "B";
            } else if (score >= 70) {
                grade = "C";
            } else {
                grade = "F";
            }
            System.out.println("score " + score + " -> " + grade);
        }

        // Dangling-else trap, shown with explicit braces to stay correct
        System.out.println("\n=== Nested if (braces avoid dangling-else) ===");
        int a = 5, b = -3;
        if (a > 0) {
            if (b > 0) {
                System.out.println("both positive");
            } else {
                System.out.println("a positive, b not");
            }
        } else {
            System.out.println("a not positive");
        }

        // Unboxing null Boolean throws NPE - demonstrated safely
        System.out.println("\n=== Unboxing null Boolean ===");
        Boolean flag = null;
        try {
            if (flag) {
                System.out.println("unreachable");
            }
        } catch (NullPointerException e) {
            System.out.println("caught NPE from unboxing null flag");
        }
    }
}
