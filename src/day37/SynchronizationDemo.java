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
