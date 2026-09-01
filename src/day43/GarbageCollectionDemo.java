import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;

public class GarbageCollectionDemo {

    public static void main(String[] args) throws InterruptedException {
        Runtime runtime = Runtime.getRuntime();

        System.out.println("Max heap (-Xmx, or JVM default): ~" + runtime.maxMemory() / (1024 * 1024) + " MB");
        for (GarbageCollectorMXBean gcBean : ManagementFactory.getGarbageCollectorMXBeans()) {
            System.out.println("GC in use: " + gcBean.getName());
        }

        printMemory(runtime, "Before allocation");

        Object obj = allocateBigArray();
        printMemory(runtime, "After allocation (still reachable)");

        obj = null; // no more references anywhere -- now eligible for collection
        System.gc(); // only a suggestion -- the JVM may or may not act on it immediately
        Thread.sleep(100);

        printMemory(runtime, "After dropping the reference and suggesting GC");
    }

    static Object allocateBigArray() {
        return new byte[50_000_000]; // ~50 MB, easy to see the effect on used memory
    }

    static void printMemory(Runtime runtime, String label) {
        long usedMb = (runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024);
        System.out.println(label + ": ~" + usedMb + " MB used");
    }
}
