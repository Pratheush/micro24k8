package com.mylearning.inventoryservice.controller;

import com.mylearning.inventoryservice.service.InventoryService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    // http:'//localhost:8082/api/inventory/iphone-13,iphone13-red

    // http:'//localhost:8082/api/inventory?skuCode=iphone-13&skuCode=iphone13-red
    /*@GetMapping("/{sku-code}")
    @ResponseStatus(HttpStatus.OK)
    public boolean isInStock(@PathVariable("sku-code") String skuCode) {

        return inventoryService.isInStock(skuCode);
    }*/

    /*@GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<InventoryResponse> isInStock(@RequestParam List<String> skuCode) {

        return inventoryService.isInStock(skuCode);
    }*/

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Boolean isInStock(@RequestParam String skuCode, @RequestParam Integer quantity) {
        return inventoryService.isInStock(skuCode, quantity);
    }
}

