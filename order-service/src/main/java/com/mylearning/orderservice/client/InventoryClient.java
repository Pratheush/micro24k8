package com.mylearning.orderservice.client;

import com.mylearning.orderservice.config.FeignConfig;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;

import java.util.concurrent.CompletableFuture;

//@FeignClient(value = "inventory", url = "${inventory.url}", configuration = FeignConfig.class)
public interface InventoryClient {
    /*@RequestMapping(method = RequestMethod.GET, value = "/api/inventory")
    boolean isInStock(@RequestParam String skuCode, @RequestParam Integer quantity);*/

    // @GetMapping did not work as feign
    /*@GetMapping(value = "/api/inventory")
    boolean isInStock(@RequestParam List<String> skuCode, @RequestParam List<Integer> quantity);*/


    /*
    ERROR HAPPENED WHEN I USED THE BELOW METHOD WITH @TimeLimiter annotation and RETURN TYPE OF METHOD WITH boolean OR Boolean
    TO SOLVE THE ERROR, METHOD WHICH IS ANNOTATED WITH @TimeLimiter SHOULD RETURN CompletableFuture<T> NOT OTHER RETUNR TYPES
    io.github.resilience4j.spring6.timelimiter.configure.IllegalReturnTypeException:
    boolean com.mylearning.orderservice.client.InventoryClient#isInStock has unsupported
    by @TimeLimiter return type. CompletionStage expected.

    Methods annotated with @TimeLimiter should return a CompletionStage<T>.
    Refactor your existing methods to return CompletionStage<T> instead of other types (e.g., List<CustomBlahClass>).
    If your long-running task doesn’t naturally return a CompletionStage, consider using the decorators approach.
    Execute the long-running task and wrap its result in a Future.

    The issue here is that you are using a boolean return type for the isInStock method in your InventoryClient,
    but the @TimeLimiter annotation expects a CompletionStage or CompletableFuture return type.
    You need to change your logic to handle the CompletableFuture correctly.

    -------------------------------------------------------------------------------------------------------------

    Feign does not natively support CompletableFuture. To handle asynchronous responses properly, you might need a custom decoder.

    c.m.orderservice.client.InventoryClient  : Cannot get inventory for skucode pixel_8, failure reason: feign.codec.DecodeException:
    Error while extracting response for type [java.util.concurrent.CompletableFuture<java.lang.Boolean>] and content type [application/json]

     create a custom decoder to handle CompletableFuture<Boolean>
     Feign Configuration: Configured the InventoryClient to use the custom decoder.
     */
    final Logger log = LoggerFactory.getLogger(InventoryClient.class);

    static final String UNSTABLE_PLACE_ORDER="inventory";

    // Instead of adding @TimeLimiter annotation  we are adding time-out configuration in RestClientConfig class
    @GetExchange("/api/inventory")
    //@RequestMapping(method = RequestMethod.GET, value = "/api/inventory")
    @CircuitBreaker(name = UNSTABLE_PLACE_ORDER, fallbackMethod = "placeOrderFallback")
    @Retry(name = UNSTABLE_PLACE_ORDER, fallbackMethod = "placeOrderFallback")
    //@TimeLimiter(name = UNSTABLE_PLACE_ORDER, fallbackMethod = "placeOrderFallback")
    boolean isInStock(@RequestParam String skuCode, @RequestParam Integer quantity);

    /*default CompletableFuture<Boolean> placeOrderFallback(String skuCode, Integer quantity, RuntimeException e) {
        log.info("Cannot get inventory for skucode {}, failure reason: {}", skuCode, e.getMessage());
        // return CompletableFuture.supplyAsync(()->false);
        // return false;
        // below statement is the recommended way to return false for fallbackMethod
        // The fallbackMethod returns a CompletableFuture.completedFuture(false) when an exception occurs.
        // return CompletableFuture.completedFuture(false);
    }*/

    default boolean placeOrderFallback(String skuCode, Integer quantity, RuntimeException e) {
        log.info("Cannot get inventory for skucode {}, failure reason: {}", skuCode, e.getMessage());
        return false;
    }
}

