public class ArraysDemo {
    public static void main(String[] args) {
        // Declaring and creating arrays
        System.out.println("=== Creation ===");
        int[] scores = new int[5];
        int[] primes = {2, 3, 5, 7, 11};
        System.out.println("scores.length = " + scores.length);
        System.out.println("primes.length = " + primes.length);

        // Indexing
        System.out.println("\n=== Indexing ===");
        scores[0] = 100;
        System.out.println("scores[0] = " + scores[0]);
        System.out.println("primes[2] = " + primes[2]);

        // Out-of-bounds access throws at runtime, not compile time
        System.out.println("\n=== Out-of-bounds access ===");
        try {
            System.out.println(primes[10]);
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("caught ArrayIndexOutOfBoundsException");
        }

        // Arrays are reference types - assignment shares the same data
        System.out.println("\n=== Reference sharing ===");
        int[] a = {1, 2, 3};
        int[] b = a;
        b[0] = 99;
        System.out.println("a[0] after mutating b = " + a[0]);

        // An independent copy needs clone() or Arrays.copyOf
        System.out.println("\n=== Independent copy ===");
        int[] c = a.clone();
        c[0] = -1;
        System.out.println("a[0] unaffected by c = " + a[0]);
        System.out.println("c[0] = " + c[0]);

        // Default values by type
        System.out.println("\n=== Default values ===");
        boolean[] flags = new boolean[3];
        String[] names = new String[3];
        double[] prices = new double[3];
        System.out.println("flags[0] = " + flags[0]);
        System.out.println("names[0] = " + names[0]);
        System.out.println("prices[0] = " + prices[0]);

        // Rectangular 2D array
        System.out.println("\n=== 2D array ===");
        int[][] grid = new int[3][4];
        grid[1][2] = 7;
        System.out.println("grid[1][2] = " + grid[1][2]);
        System.out.println("grid[0][0] default = " + grid[0][0]);

        int[][] matrix = {
            {1, 2, 3},
            {4, 5, 6}
        };
        for (int row = 0; row < matrix.length; row++) {
            for (int col = 0; col < matrix[row].length; col++) {
                System.out.print(matrix[row][col] + " ");
            }
            System.out.println();
        }

        // Jagged array: rows have different lengths
        System.out.println("\n=== Jagged array ===");
        int[][] jagged = new int[3][];
        jagged[0] = new int[]{1};
        jagged[1] = new int[]{1, 2};
        jagged[2] = new int[]{1, 2, 3};
        for (int row = 0; row < jagged.length; row++) {
            System.out.println("row " + row + " length = " + jagged[row].length);
        }
    }
}
