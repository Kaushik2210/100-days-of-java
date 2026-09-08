import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

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

        List<Integer> arrayList = new ArrayList<>();
        List<Integer> linkedList = new LinkedList<>();
        for (int i = 0; i < 20_000; i++) {
            arrayList.add(i);
            linkedList.add(i);
        }

        start = System.nanoTime();
        long arraySum = 0;
        for (int i = 0; i < arrayList.size(); i++) {
            arraySum += arrayList.get(i); // O(1) per access
        }
        long arrayMillis = (System.nanoTime() - start) / 1_000_000;

        start = System.nanoTime();
        long linkedSum = 0;
        for (int i = 0; i < linkedList.size(); i++) {
            linkedSum += linkedList.get(i); // O(n) per access -- walks the list from the front each time
        }
        long linkedMillis = (System.nanoTime() - start) / 1_000_000;

        System.out.println("Sums match: " + (arraySum == linkedSum));
        System.out.println("ArrayList indexed access: " + arrayMillis + " ms");
        System.out.println("LinkedList indexed access: " + linkedMillis + " ms");
    }
}
