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
