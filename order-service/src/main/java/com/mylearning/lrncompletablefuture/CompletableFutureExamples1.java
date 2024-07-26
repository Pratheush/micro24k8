package com.mylearning.lrncompletablefuture;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class CompletableFutureExamples1 {
    public static void main(String[] args) {
        CompletableFutureExamples1 examples = new CompletableFutureExamples1();
        examples.runExamples();
    }

    public void runExamples() {
        try {
            CompletableFuture<String> f1 = waitAndReturn(1_000, "Harry");
            CompletableFuture<String> f2 = waitAndReturn(2_000, "Ron");

            // Using allOf() to combine multiple futures
            CompletableFuture<Void> combinedFutures = CompletableFuture.allOf(f1, f2);

            // Extracting values from combined futures
            combinedFutures.thenRun(() -> {
                try {
                    System.out.println("Combined result: " + f1.get() + ", " + f2.get());
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }).join();

            // Using whenComplete()
            CompletableFuture<String> future = waitAndReturn(1_500, "Hermione");
            future.whenComplete((result, throwable) -> {
                if (throwable == null) {
                    System.out.println("Result: " + result);
                } else {
                    System.err.println("Error: " + throwable.getMessage());
                }
            }).join();

            // Using exceptionally()
            CompletableFuture<String> failingFuture = CompletableFuture.supplyAsync(() -> {
                throw new RuntimeException("Something went wrong!");
            });
            failingFuture.exceptionally(throwable -> {
                System.err.println("Handled error: " + throwable.getMessage());
                return "Default value";
            }).thenAccept(System.out::println).join();

            // Using join()
            CompletableFuture<String> immediateFuture = CompletableFuture.completedFuture("Immediate value");
            String result = immediateFuture.join(); // join() waits for the completion and returns the result
            System.out.println("Join result: " + result);

            // Using thenApply() and thenAccept()
            CompletableFuture<Integer> lengthFuture = waitAndReturn(1_000, "Dumbledore")
                    .thenApply(String::length);
            lengthFuture.thenAccept(length -> System.out.println("Length: " + length)).join();

            // Using handle()
            CompletableFuture<String> handledFuture = waitAndReturn(1_000, "Snape")
                    .handle((resultValue, throwable) -> {
                        if (throwable != null) {
                            return "Error handled";
                        }
                        return resultValue.toUpperCase();
                    });
            System.out.println("Handle result: " + handledFuture.join());

            // Additional Async Methods
            demonstrateAsyncMethods();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Utility method to simulate async processing with delay
    private CompletableFuture<String> waitAndReturn(int delayMillis, String value) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                TimeUnit.MILLISECONDS.sleep(delayMillis);
            } catch (InterruptedException e) {
                throw new IllegalStateException(e);
            }
            return value;
        });
    }

    private void demonstrateAsyncMethods() {
        // Using thenApplyAsync()
        CompletableFuture<String> asyncApplyFuture = waitAndReturn(1_000, "Hello")
                .thenApplyAsync(value -> value + " World");
        asyncApplyFuture.thenAccept(result -> System.out.println("Async Apply Result: " + result)).join();

        // Using thenAcceptAsync()
        CompletableFuture<Void> asyncAcceptFuture = waitAndReturn(1_000, "Hello")
                .thenAcceptAsync(value -> System.out.println("Async Accept Result: " + value));
        asyncAcceptFuture.join();

        // Using thenRunAsync()
        CompletableFuture<Void> asyncRunFuture = waitAndReturn(1_000, "Hello")
                .thenRunAsync(() -> System.out.println("Async Run Completed"));
        asyncRunFuture.join();

        // Using supplyAsync()
        CompletableFuture<String> supplyAsyncFuture = CompletableFuture.supplyAsync(() -> {
            try {
                TimeUnit.MILLISECONDS.sleep(1_000);
            } catch (InterruptedException e) {
                throw new IllegalStateException(e);
            }
            return "SupplyAsync Result";
        });
        System.out.println(supplyAsyncFuture.join());

        // Using runAsync()
        CompletableFuture<Void> runAsyncFuture = CompletableFuture.runAsync(() -> {
            try {
                TimeUnit.MILLISECONDS.sleep(1_000);
            } catch (InterruptedException e) {
                throw new IllegalStateException(e);
            }
            System.out.println("RunAsync Completed");
        });
        runAsyncFuture.join();

        // Chaining async methods
        CompletableFuture<String> chainedFuture = waitAndReturn(1_000, "Chain")
                .thenApplyAsync(value -> value + " of")
                .thenApplyAsync(value -> value + " Futures");
        chainedFuture.thenAccept(result -> System.out.println("Chained Async Result: " + result)).join();
    }
}
