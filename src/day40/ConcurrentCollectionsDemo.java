import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;

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

        List<String> subscribers = new CopyOnWriteArrayList<>();
        subscribers.add("alice@example.com");
        subscribers.add("bob@example.com");
        for (String subscriber : subscribers) {
            System.out.println("Notifying " + subscriber);
        }

        BlockingQueue<Integer> queue = new LinkedBlockingQueue<>(10);

        Thread producer = new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                try {
                    queue.put(i); // blocks if the queue is full
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });

        Thread consumer = new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                try {
                    System.out.println("Consumed: " + queue.take()); // blocks if the queue is empty
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });

        producer.start();
        consumer.start();
        producer.join();
        consumer.join();
    }
}
