public class PerformanceDemo {

    public static void main(String[] args) {
        int iterations = 50_000;

        long start = System.nanoTime();
        String result = "";
        for (int i = 0; i < iterations; i++) {
            result += i; // creates a new String every iteration
        }
        long concatMillis = (System.nanoTime() - start) / 1_000_000;

        start = System.nanoTime();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < iterations; i++) {
            sb.append(i); // grows an internal buffer -- no new object per iteration
        }
        String result2 = sb.toString();
        long builderMillis = (System.nanoTime() - start) / 1_000_000;

        System.out.println("Both results have the same length: " + (result.length() == result2.length()));
        System.out.println("String concat: " + concatMillis + " ms");
        System.out.println("StringBuilder: " + builderMillis + " ms");
    }
}
