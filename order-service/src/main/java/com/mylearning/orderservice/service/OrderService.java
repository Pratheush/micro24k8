package com.mylearning.orderservice.service;

/*import com.mylearning.orderservice.dto.InventoryResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import com.mylearning.orderservice.event.OrderPlacedEvent;
import org.springframework.beans.factory.annotation.Autowired;
import brave.Span;
import brave.Tracer;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.Arrays;*/
import com.mylearning.orderservice.client.InventoryClient;
import com.mylearning.orderservice.exception.ProductNotFoundException;
import com.mylearning.orderservice.model.Order;
import com.mylearning.orderservice.repository.OrderRepository;
import com.mylearning.orderservice.dto.OrderRequest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
@Transactional
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;

    private final InventoryClient inventoryClient;

    @Autowired
    public OrderService(OrderRepository orderRepository, InventoryClient inventoryClient) {
        this.orderRepository = orderRepository;
        this.inventoryClient=inventoryClient;
    }

    // if we use CompletableFuture<Boolean> as return type for InventoryClient abstract method then use below method
    /*public void placeOrder(OrderRequest orderRequest) {
        CompletableFuture<Boolean> inStockFuture = inventoryClient.isInStock(orderRequest.skuCode(), orderRequest.quantity());

        // thenAccept is used to handle the result of the CompletableFuture.
        inStockFuture.thenAccept(inStock ->{
            log.info("Order-Service placeOrder -- inStock:: {}", inStock);
            if (inStock) {
                log.info("Order-Service placeOrder -- if Block");
                var order = mapToOrder(orderRequest);
                orderRepository.save(order);
                log.info("Order-Service placeOrder -- if Block order Saved: {}", order);
            }else {
                log.info("Order-Service placeOrder -- else Block");
                throw new ProductNotFoundException("Product with SkuCode : " + orderRequest.skuCode() + " is not in Stock");
            }
        }).exceptionally(ex ->{
            // exceptionally is used to handle any exceptions that occur
            log.error("Error placing order", ex);
            throw new RuntimeException("Error placing order", ex);
        }).join();  // This will block until the CompletableFuture completes. join() is used to block and wait for the CompletableFuture to complete.
    }*/

    // when InventoryClient abstract method return type is Boolean then use below method
    public void placeOrder(OrderRequest orderRequest) {
        boolean inStock = inventoryClient.isInStock(orderRequest.skuCode(), orderRequest.quantity());
        log.info("Order-Service placeOrder -- inStock:: {}", inStock);
         if (!inStock){
             log.info("Order-Service placeOrder -- if Block");
             throw new ProductNotFoundException("Product with SkuCode : " + orderRequest.skuCode() + " is not in Stock");
         }
         var order = mapToOrder(orderRequest);
         orderRepository.save(order);
         log.info("Order-Service placeOrder -- order Saved: {}", order);

    }

    private static Order mapToOrder(OrderRequest orderRequest) {
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());
        order.setPrice(orderRequest.price());
        order.setQuantity(orderRequest.quantity());
        order.setSkuCode(orderRequest.skuCode());
        return order;
    }
}
