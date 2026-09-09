public class BigODemo {

    public static void main(String[] args) {
        int[] arr = {5, 3, 8, 1, 9, 2};

        System.out.println("firstElement (O(1)): " + firstElement(arr));
        System.out.println("sum (O(n)): " + sum(arr));
        System.out.println("hasDuplicate (O(n^2)) on [5,3,8,1,9,2]: " + hasDuplicate(arr));

        int[] withDup = {5, 3, 8, 3, 9, 2};
        System.out.println("hasDuplicate (O(n^2)) on [5,3,8,3,9,2]: " + hasDuplicate(withDup));

        // warm up the JIT so the timed runs below reflect steady-state performance, not compilation overhead
        for (int i = 0; i < 5; i++) {
            int[] warmup = new int[4_000];
            sum(warmup);
            hasDuplicate(warmup);
        }

        System.out.println();
        System.out.println("Doubling n, comparing how O(n) sum vs O(n^2) hasDuplicate scale:");
        for (int n : new int[]{4_000, 8_000, 16_000}) {
            int[] data = new int[n]; // all distinct values, so hasDuplicate always runs its full worst case
            for (int i = 0; i < n; i++) data[i] = i;

            long start = System.nanoTime();
            sum(data);
            long sumMicros = (System.nanoTime() - start) / 1_000;

            start = System.nanoTime();
            hasDuplicate(data);
            long dupMillis = (System.nanoTime() - start) / 1_000_000;

            System.out.println("n=" + n + "  sum: " + sumMicros + " us   hasDuplicate: " + dupMillis + " ms");
        }
    }

    // O(1) -- constant time: cost doesn't depend on input size at all
    static int firstElement(int[] arr) {
        return arr[0];
    }

    // O(n) -- linear: cost grows proportionally with input size
    static int sum(int[] arr) {
        int total = 0;
        for (int x : arr) {
            total += x;
        }
        return total;
    }

    // O(n^2) -- quadratic: nested loop over the same input
    static boolean hasDuplicate(int[] arr) {
        for (int i = 0; i < arr.length; i++) {
            for (int j = i + 1; j < arr.length; j++) {
                if (arr[i] == arr[j]) return true;
            }
        }
        return false;
    }
}
