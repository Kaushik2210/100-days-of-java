import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ConcurrentCollectionsDemo {

    public static void main(String[] args) throws InterruptedException {
        Map<String, Integer> counts = new ConcurrentHashMap<>();

        Runnable incrementer = () -> {
            for (int i = 0; i < 10_000; i++) {
                counts.merge("total", 1, Integer::sum); // atomic read-modify-write, safe across threads
            }
        };

        Thread t1 = new Thread(incrementer);
        Thread t2 = new Thread(incrementer);
        t1.start();
        t2.start();
        t1.join();
        t2.join();

        System.out.println("total = " + counts.get("total")); // always 20000
    }
}
