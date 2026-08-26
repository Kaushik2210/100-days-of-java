import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class ExecutorsDemo {

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        ExecutorService pool = Executors.newFixedThreadPool(4); // 4 worker threads, reused across tasks

        for (int i = 0; i < 10; i++) {
            int taskNumber = i;
            pool.submit(() -> {
                System.out.println("Task " + taskNumber + " on " + Thread.currentThread().getName());
            });
        }

        pool.shutdown(); // stops accepting new tasks; already-submitted ones still run to completion
        pool.awaitTermination(5, TimeUnit.SECONDS); // wait for the demo output to finish before exiting
        System.out.println("All tasks completed");

        ExecutorService resultPool = Executors.newFixedThreadPool(2);

        Callable<Integer> task = () -> {
            Thread.sleep(100); // simulate work
            return 21 * 2;
        };

        Future<Integer> future = resultPool.submit(task); // returns immediately -- doesn't block

        System.out.println("Doing other work while the task runs...");
        Integer result = future.get(); // blocks here until the result is ready
        System.out.println("Result: " + result);

        resultPool.shutdown();
    }
}
