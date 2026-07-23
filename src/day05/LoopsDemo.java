public class LoopsDemo {
    public static void main(String[] args) {
        // Classic for loop
        System.out.println("=== Classic for ===");
        for (int i = 0; i < 5; i++) {
            System.out.println("i = " + i);
        }

        // Multiple loop variables
        System.out.println("\n=== Multi-variable for ===");
        for (int i = 0, j = 10; i < j; i++, j--) {
            System.out.println("i=" + i + " j=" + j);
        }

        // Enhanced for-each loop
        System.out.println("\n=== for-each ===");
        int[] nums = {2, 4, 6, 8};
        for (int n : nums) {
            System.out.println(n);
        }

        // Mutating the for-each variable does not affect the array
        System.out.println("\n=== for-each does not mutate source ===");
        for (int n : nums) {
            n = n * 100;
        }
        for (int n : nums) {
            System.out.println("still " + n);
        }

        // while loop: condition checked before the body
        System.out.println("\n=== while ===");
        int count = 0;
        while (count < 3) {
            System.out.println("count = " + count);
            count++;
        }

        // while whose condition is false immediately never runs
        System.out.println("\n=== while that never runs ===");
        int skip = 10;
        while (skip < 3) {
            System.out.println("unreachable");
        }
        System.out.println("loop body never executed");

        // do-while loop: body runs at least once
        System.out.println("\n=== do-while ===");
        int attempts = 0;
        do {
            System.out.println("attempt " + attempts);
            attempts++;
        } while (attempts < 3);

        // do-while runs once even when the condition starts false
        System.out.println("\n=== do-while runs at least once ===");
        int neverTrue = 100;
        do {
            System.out.println("ran despite condition being false");
        } while (neverTrue < 3);

        // break exits the nearest enclosing loop entirely
        System.out.println("\n=== break ===");
        for (int i = 0; i < 10; i++) {
            if (i == 4) {
                break;
            }
            System.out.println("i = " + i);
        }

        // continue skips to the next iteration
        System.out.println("\n=== continue ===");
        for (int i = 0; i < 6; i++) {
            if (i % 2 == 0) {
                continue;
            }
            System.out.println("odd i = " + i);
        }

        // labeled break/continue control an outer loop from an inner one
        System.out.println("\n=== labeled loops ===");
        outer:
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                if (row == 1 && col == 1) {
                    System.out.println("skipping rest of row " + row);
                    continue outer;
                }
                if (row == 2 && col == 0) {
                    System.out.println("stopping entirely at row " + row);
                    break outer;
                }
                System.out.println("row=" + row + " col=" + col);
            }
        }
    }
}
