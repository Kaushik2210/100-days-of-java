import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Random;

public class AmortizedAnalysisDemo {

    /** Dynamic array that doubles when full and counts every element write (append or copy). */
    static class CountingArray {
        private int[] data;
        private int size;
        private final double growth;
        long writes;
        int resizes;

        CountingArray(int initialCapacity, double growth) {
            this.data = new int[initialCapacity];
            this.growth = growth;
        }

        void add(int value) {
            if (size == data.length) {
                int newCapacity = Math.max(data.length + 1, (int) Math.ceil(data.length * growth));
                int[] bigger = new int[newCapacity];
                for (int i = 0; i < size; i++) {
                    bigger[i] = data[i];
                    writes++;
                }
                data = bigger;
                resizes++;
            }
            data[size++] = value;
            writes++;
        }
    }

    /** Binary counter stored as bits; counts every bit flip. */
    static class BinaryCounter {
        private final boolean[] bits;
        long flips;

        BinaryCounter(int width) {
            bits = new boolean[width];
        }

        void increment() {
            int i = 0;
            while (i < bits.length && bits[i]) {
                bits[i] = false;
                flips++;
                i++;
            }
            if (i < bits.length) {
                bits[i] = true;
                flips++;
            }
        }
    }

    /** Queue built from two stacks; counts every element move between stacks. */
    static class TwoStackQueue {
        private final Deque<Integer> in = new ArrayDeque<>();
        private final Deque<Integer> out = new ArrayDeque<>();
        long moves;

        void enqueue(int x) {
            in.push(x);
        }

        int dequeue() {
            if (out.isEmpty()) {
                while (!in.isEmpty()) {
                    out.push(in.pop());
                    moves++;
                }
            }
            return out.pop();
        }
    }

    public static void main(String[] args) {
        System.out.println("== Dynamic array: doubling vs growing by a constant ==");
        for (int n : new int[] {1_000, 10_000, 100_000, 1_000_000}) {
            CountingArray doubling = new CountingArray(1, 2.0);
            CountingArray plusOne = new CountingArray(1, 1.0); // always +1 capacity
            CountingArray oneAndHalf = new CountingArray(1, 1.5);
            for (int i = 0; i < n; i++) {
                doubling.add(i);
                oneAndHalf.add(i);
            }
            if (n <= 10_000) {
                for (int i = 0; i < n; i++) plusOne.add(i);
            }
            System.out.printf("n=%,9d  doubling: %.2f writes/add (%2d resizes)   x1.5: %.2f writes/add",
                n, (double) doubling.writes / n, doubling.resizes, (double) oneAndHalf.writes / n);
            if (n <= 10_000) System.out.printf("   +1 each time: %.1f writes/add", (double) plusOne.writes / n);
            System.out.println();
        }
        System.out.println("doubling stays under 3 writes per add (the bound is 1 + 2 = 3); growing by +1 grows with n.");

        System.out.println();
        System.out.println("== Worst single add vs average add ==");
        CountingArray probe = new CountingArray(1, 2.0);
        long worst = 0;
        int worstAt = 0;
        for (int i = 1; i <= 1 << 16; i++) {
            long before = probe.writes;
            probe.add(i);
            long cost = probe.writes - before;
            if (cost > worst) {
                worst = cost;
                worstAt = i;
            }
        }
        System.out.println("n=65536: worst single add cost " + worst + " writes (add #" + worstAt
            + "), average " + String.format("%.2f", (double) probe.writes / (1 << 16)));

        System.out.println();
        System.out.println("== Binary counter: bit flips per increment ==");
        for (int n : new int[] {10, 1_000, 1_000_000}) {
            BinaryCounter counter = new BinaryCounter(32);
            for (int i = 0; i < n; i++) counter.increment();
            System.out.printf("n=%,9d  total flips=%,9d  per increment=%.3f  (bound: 2n = %,d)%n",
                n, counter.flips, (double) counter.flips / n, 2L * n);
        }

        System.out.println();
        System.out.println("== Two-stack queue: moves per operation ==");
        Random random = new Random(93); // fixed seed so the run is repeatable
        TwoStackQueue queue = new TwoStackQueue();
        Deque<Integer> reference = new ArrayDeque<>();
        int ops = 1_000_000;
        int enqueues = 0;
        int mismatches = 0;
        for (int i = 0; i < ops; i++) {
            if (reference.isEmpty() || random.nextInt(100) < 55) {
                queue.enqueue(i);
                reference.addLast(i);
                enqueues++;
            } else if (queue.dequeue() != reference.removeFirst()) {
                mismatches++;
            }
        }
        System.out.println("matches a reference queue over " + ops + " random operations: "
            + (mismatches == 0 ? "yes" : "NO, " + mismatches + " mismatches"));
        System.out.printf("enqueues=%,d  element moves=%,d  (each element moves at most once, so moves <= enqueues: %b)%n",
            enqueues, queue.moves, queue.moves <= enqueues);
    }
}
