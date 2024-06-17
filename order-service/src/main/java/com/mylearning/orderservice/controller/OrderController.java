package com.mylearning.orderservice.controller;

import com.mylearning.orderservice.dto.OrderRequest;
import com.mylearning.orderservice.service.OrderService;
/*import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;*/
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/order")
public class OrderController {

    private final OrderService orderService;

    private static final String UNSTABLE_PLACE_ORDER="unstablePlaceOrder";

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    // we have to return CompletableFuture instead of String because this will make an asynchronous call  we have to return a CompletableFuture
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
  /*  @CircuitBreaker(name=UNSTABLE_PLACE_ORDER,fallbackMethod = "placeOrderFallback")
    @TimeLimiter(name = UNSTABLE_PLACE_ORDER)
    @Retry(name = UNSTABLE_PLACE_ORDER)*/
    //public CompletableFuture<String> placeOrder(@RequestBody OrderRequest orderRequest) {
    public String placeOrder(@RequestBody OrderRequest orderRequest) {
        return orderService.placeOrder_24(orderRequest);
        //return "Order Placed Successfully";
        //return CompletableFuture.supplyAsync(() -> orderService.placeOrder(orderRequest));
    }

    public CompletableFuture<String> placeOrderFallback(OrderRequest orderRequest,RuntimeException e){
        //System.out.println(e.getMessage());
        //return "Oops! Something went wrong, please order after some time!";
        return CompletableFuture.supplyAsync(() -> "Oops! Something went wrong, please order after some time!");
    }
}
