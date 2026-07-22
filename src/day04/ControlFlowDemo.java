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

        // Classic switch statement with grouped labels and fall-through
        System.out.println("\n=== Classic switch ===");
        for (int day = 1; day <= 7; day++) {
            switch (day) {
                case 1:
                case 2:
                case 3:
                case 4:
                case 5:
                    System.out.println("day " + day + " -> weekday");
                    break;
                case 6:
                case 7:
                    System.out.println("day " + day + " -> weekend");
                    break;
                default:
                    System.out.println("day " + day + " -> invalid");
            }
        }

        // Arrow-form switch expression with yield
        System.out.println("\n=== Switch expression (arrow form) ===");
        for (int day = 1; day <= 7; day++) {
            String description = switch (day) {
                case 1, 2, 3, 4, 5 -> {
                    String label = "weekday";
                    yield label + " (day " + day + ")";
                }
                case 6, 7 -> "weekend";
                default -> "invalid";
            };
            System.out.println(description);
        }
    }
}
