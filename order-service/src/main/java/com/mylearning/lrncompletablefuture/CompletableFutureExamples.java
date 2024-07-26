package com.mylearning.lrncompletablefuture;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
public class CompletableFutureExamples {

    public static void main(String[] args) {
        CompletableFutureExamples examples = new CompletableFutureExamples();
        examples.runExamples();
    }
    public void runExamples() {
        try {
            CompletableFuture<String> f1 = waitAndReturn(1_000, "Harry");
            CompletableFuture<String> f2 = waitAndReturn(2_000, "Ron");

            // Using allOf() to combine multiple futures
            // Combines multiple CompletableFuture instances into a single CompletableFuture<Void> that completes when all of the given futures complete.
            CompletableFuture<Void> combinedFutures = CompletableFuture.allOf(f1, f2);

            // Waits if necessary for this future to complete, and then returns its result.
            // Extracting values from combined futures
            combinedFutures.thenRun(() -> {
                try {
                    System.out.println("Combined result: " + f1.get() + ", " + f2.get());
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }).join(); // Waits for the CompletableFuture to complete and returns the result. It throws an unchecked exception if the computation resulted in an exception.

            // Using whenComplete()
            // Allows you to specify a callback to be executed when the CompletableFuture completes, either successfully or with an exception.
            CompletableFuture<String> future = waitAndReturn(1_500, "Hermione");
            future.whenComplete((result, throwable) -> {
                if (throwable == null) {
                    System.out.println("Result: " + result);
                } else {
                    System.err.println("Error: " + throwable.getMessage());
                }
            }).join();

            // Using exceptionally()
            // Allows you to handle exceptions and provide a fallback result if the CompletableFuture completes exceptionally.
            CompletableFuture<String> failingFuture = CompletableFuture.supplyAsync(() -> {
                throw new RuntimeException("Something went wrong!");
            });
            failingFuture.exceptionally(throwable -> {
                System.err.println("Handled error: " + throwable.getMessage());
                return "Default value";
            }).thenAccept(System.out::println).join(); // // thenAccept() : Consumes the result of the CompletableFuture using a consumer.

            // Using join()
            // create an already completed CompletableFuture with a predefined result.
            CompletableFuture<String> immediateFuture = CompletableFuture.completedFuture("Immediate value");
            String result = immediateFuture.join(); // join() waits for the completion and returns the result
            System.out.println("Join result: " + result);

            // The getNow(null) returns the result if completed, otherwise it returns null1.
            CompletableFuture<String> cf = CompletableFuture.completedFuture("message");
            assertTrue(cf.isDone());
            assertEquals("message", cf.getNow(null));

            // Using thenApply() and thenAccept()
            // thenApply() :: Transforms the result of the CompletableFuture using a function
            // thenAccept() : Consumes the result of the CompletableFuture using a consumer.
            CompletableFuture<Integer> lengthFuture = waitAndReturn(1_000, "Dumbledore")
                    .thenApply(String::length);
            lengthFuture.thenAccept(length -> System.out.println("Length: " + length)).join();

            // Using handle()
            // Allows you to handle the result or the exception of the CompletableFuture and return a new result.
            CompletableFuture<String> handledFuture = waitAndReturn(1_000, "Snape")
                    .handle((resultValue, throwable) -> {
                        if (throwable != null) {
                            return "Error handled";
                        }
                        return resultValue.toUpperCase();
                    });
            System.out.println("Handle result: " + handledFuture.join());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    // Utility method to simulate async processing with delay
    private CompletableFuture<String> waitAndReturn(int delayMillis, String value) {
        log.info("waitAndReturn() :: delayMillis: {} , value: {}",  delayMillis,value);
        return CompletableFuture.supplyAsync(() -> {
            try {
                TimeUnit.MILLISECONDS.sleep(delayMillis);
            } catch (InterruptedException e) {
                throw new IllegalStateException(e);
            }
            return value;
        });
    }
}
