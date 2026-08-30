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
    }
}
