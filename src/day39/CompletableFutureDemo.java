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

        CompletableFuture<Integer> userId = CompletableFuture.supplyAsync(() -> 42);
        CompletableFuture<String> profile = userId.thenCompose(id ->
            CompletableFuture.supplyAsync(() -> "Profile for user " + id)
        );
        System.out.println(profile.join());

        CompletableFuture<Integer> price = CompletableFuture.supplyAsync(() -> 100);
        CompletableFuture<Double> taxRate = CompletableFuture.supplyAsync(() -> 0.08);
        CompletableFuture<Double> total = price.thenCombine(taxRate, (p, rate) -> p * (1 + rate));
        System.out.println("total = " + total.join());

        CompletableFuture<Integer> risky = CompletableFuture.supplyAsync(() -> {
            throw new RuntimeException("boom");
        });
        CompletableFuture<Integer> recovered = risky.exceptionally(ex -> {
            System.out.println("Recovered from: " + ex.getMessage());
            return -1;
        });
        System.out.println("recovered = " + recovered.join());
    }

    static void sleepQuietly(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
