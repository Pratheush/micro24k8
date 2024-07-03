package com.mylearning.orderservice.client;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;

@FeignClient(value = "inventory", url = "${inventory.url}")
public interface InventoryClient {
    /*@RequestMapping(method = RequestMethod.GET, value = "/api/inventory")
    boolean isInStock(@RequestParam String skuCode, @RequestParam Integer quantity);*/

    // @GetMapping did not work as feign
    /*@GetMapping(value = "/api/inventory")
    boolean isInStock(@RequestParam List<String> skuCode, @RequestParam List<Integer> quantity);*/

    final Logger log = LoggerFactory.getLogger(InventoryClient.class);

    static final String UNSTABLE_PLACE_ORDER="unstablePlaceOrder";

    //@GetExchange("/api/inventory")
    @RequestMapping(method = RequestMethod.GET, value = "/api/inventory")
    @CircuitBreaker(name = UNSTABLE_PLACE_ORDER, fallbackMethod = "fallbackMethod")
    @Retry(name = UNSTABLE_PLACE_ORDER)
    boolean isInStock(@RequestParam String skuCode, @RequestParam Integer quantity);

    default boolean fallbackMethod(String skuCode, Integer quantity, RuntimeException e) {
        log.info("Cannot get inventory for skucode {}, failure reason: {}", skuCode, e.getMessage());
        return false;
    }
}