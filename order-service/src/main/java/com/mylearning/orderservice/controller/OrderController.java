package com.mylearning.orderservice.controller;

import com.mylearning.orderservice.dto.OrderRequest;
import com.mylearning.orderservice.service.OrderService;
/*import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;*/
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/order")
@Slf4j
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public String placeOrder(@RequestBody OrderRequest orderRequest) {
        log.info("OrderController.placeOrder :: {}", orderRequest);
        orderService.placeOrder(orderRequest);
        log.info("OrderController.placeOrder :: order placed Successfully");
        return "Order Placed Successfully";
    }
}
