## CompletableFutureExample 
Explanation of CompletableFuture Methods
allOf()

CompletableFuture<Void> combinedFutures = CompletableFuture.allOf(f1, f2);
Combines multiple CompletableFuture instances into a single CompletableFuture<Void> that completes when all of the given futures complete.
whenComplete()

future.whenComplete((result, throwable) -> { ... });
Allows you to specify a callback to be executed when the CompletableFuture completes, either successfully or with an exception.
exceptionally()

failingFuture.exceptionally(throwable -> { ... });
Allows you to handle exceptions and provide a fallback result if the CompletableFuture completes exceptionally.
join()

String result = immediateFuture.join();
Waits for the CompletableFuture to complete and returns the result. It throws an unchecked exception if the computation resulted in an exception.
thenApply()

CompletableFuture<Integer> lengthFuture = waitAndReturn(1_000, "Dumbledore").thenApply(String::length);
Transforms the result of the CompletableFuture using a function.
thenAccept()

lengthFuture.thenAccept(length -> System.out.println("Length: " + length)).join();
Consumes the result of the CompletableFuture using a consumer.
handle()

CompletableFuture<String> handledFuture = waitAndReturn(1_000, "Snape").handle((resultValue, throwable) -> { ... });
Allows you to handle the result or the exception of the CompletableFuture and return a new result.

***
***

## CompletableFutureExamples1

thenApplyAsync()

CompletableFuture<String> asyncApplyFuture = waitAndReturn(1_000, "Hello").thenApplyAsync(value -> value + " World");
Similar to thenApply(), but runs the transformation function asynchronously.
thenAcceptAsync()

CompletableFuture<Void> asyncAcceptFuture = waitAndReturn(1_000, "Hello").thenAcceptAsync(value -> System.out.println("Async Accept Result: " + value));
Similar to thenAccept(), but runs the consumer function asynchronously.
thenRunAsync()

CompletableFuture<Void> asyncRunFuture = waitAndReturn(1_000, "Hello").thenRunAsync(() -> System.out.println("Async Run Completed"));
Runs a Runnable task asynchronously after the CompletableFuture completes.
supplyAsync()

CompletableFuture<String> supplyAsyncFuture = CompletableFuture.supplyAsync(() -> { ... });
Starts an asynchronous computation that returns a result.
runAsync()

CompletableFuture<Void> runAsyncFuture = CompletableFuture.runAsync(() -> { ... });
Starts an asynchronous computation that does not return a result.
Chaining Async Methods

CompletableFuture<String> chainedFuture = waitAndReturn(1_000, "Chain").thenApplyAsync(value -> value + " of").thenApplyAsync(value -> value + " Futures");
Demonstrates chaining multiple asynchronous transformations.