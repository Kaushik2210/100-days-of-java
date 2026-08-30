import java.util.concurrent.atomic.AtomicInteger;

public class AtomicAndVolatileDemo {

    public static void main(String[] args) throws InterruptedException {
        AtomicInteger counter = new AtomicInteger(0);

        Runnable incrementer = () -> {
            for (int i = 0; i < 100_000; i++) {
                counter.incrementAndGet(); // atomic, lock-free
            }
        };

        Thread t1 = new Thread(incrementer);
        Thread t2 = new Thread(incrementer);
        t1.start();
        t2.start();
        t1.join();
        t2.join();

        System.out.println("counter = " + counter.get()); // always 200000

        Flag flag = new Flag();
        Thread worker = new Thread(() -> {
            while (flag.running) {
                // busy work, checking the volatile field every iteration
            }
            System.out.println("Worker stopped");
        });
        worker.start();
        Thread.sleep(100);
        flag.running = false; // guaranteed to be visible to the worker thread because it's volatile
        worker.join();
        System.out.println("Main thread done");
    }
}

class Flag {
    volatile boolean running = true; // every thread always sees the latest write
}
