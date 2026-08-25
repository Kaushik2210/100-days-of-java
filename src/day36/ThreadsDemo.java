public class ThreadsDemo {

    public static void main(String[] args) throws InterruptedException {
        Thread t1 = new GreetingThread();
        Thread t2 = new Thread(new GreetingTask());
        Thread t3 = new Thread(() -> System.out.println("Hello from " + Thread.currentThread().getName()));

        t1.start();
        t2.start();
        t3.start();

        t1.join();
        t2.join();
        t3.join();

        System.out.println("All threads finished");

        Thread worker = new Thread(() -> {
            System.out.println("Working...");
        });
        worker.start();
        worker.join(); // main thread waits here until worker finishes
        System.out.println("Worker is done"); // guaranteed to print after "Working..."

        System.out.println("Before sleep");
        Thread.sleep(200); // pauses this thread briefly
        System.out.println("After sleep");

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
        // expected 200000, but usually prints less -- a race condition on the unsynchronized field
        System.out.println("counter = " + counter.count);
    }
}

class Counter {
    int count;

    void increment() {
        count++; // not atomic -- read, add one, write back, three separate steps
    }
}

class GreetingThread extends Thread {
    @Override
    public void run() {
        System.out.println("Hello from " + Thread.currentThread().getName());
    }
}

class GreetingTask implements Runnable {
    @Override
    public void run() {
        System.out.println("Hello from " + Thread.currentThread().getName());
    }
}
