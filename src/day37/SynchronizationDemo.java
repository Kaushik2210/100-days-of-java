import java.util.concurrent.locks.ReentrantLock;

public class SynchronizationDemo {

    public static void main(String[] args) throws InterruptedException {
        Counter counter = new Counter();

        Thread incrementerA = new Thread(() -> {
            for (int i = 0; i < 100_000; i++) counter.increment();
        });
        Thread incrementerB = new Thread(() -> {
            for (int i = 0; i < 100_000; i++) counter.increment();
        });

        incrementerA.start();
        incrementerB.start();
        incrementerA.join();
        incrementerB.join();

        System.out.println("counter = " + counter.get()); // always 200000 now -- synchronized fixes the race

        LockCounter lockCounter = new LockCounter();
        Thread lockA = new Thread(() -> {
            for (int i = 0; i < 100_000; i++) lockCounter.increment();
        });
        Thread lockB = new Thread(() -> {
            for (int i = 0; i < 100_000; i++) lockCounter.increment();
        });
        lockA.start();
        lockB.start();
        lockA.join();
        lockB.join();

        System.out.println("lockCounter = " + lockCounter.get()); // also always 200000
    }
}

class Counter {
    private int count;

    synchronized void increment() { // acquires this object's lock before running
        count++;
    }

    synchronized int get() {
        return count;
    }
}

class LockCounter {
    private int count;
    private final ReentrantLock lock = new ReentrantLock();

    void increment() {
        lock.lock();
        try {
            count++;
        } finally {
            lock.unlock(); // MUST be in finally -- unlocking is never automatic
        }
    }

    int get() {
        lock.lock();
        try {
            return count;
        } finally {
            lock.unlock();
        }
    }
}
