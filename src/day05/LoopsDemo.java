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
    }
}
