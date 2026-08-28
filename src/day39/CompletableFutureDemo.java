import java.util.concurrent.CompletableFuture;

public class CompletableFutureDemo {

    public static void main(String[] args) {
        CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> {
            sleepQuietly(100); // simulate work
            return 21 * 2;
        }); // runs on a background thread -- returns immediately

        future.thenAccept(result -> System.out.println("Result: " + result))
              .join(); // block here only so main doesn't exit before the async callback runs

        CompletableFuture<String> chained = CompletableFuture
            .supplyAsync(() -> 21 * 2)
            .thenApply(n -> "The answer is " + n);

        System.out.println(chained.join());
    }

    static void sleepQuietly(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
