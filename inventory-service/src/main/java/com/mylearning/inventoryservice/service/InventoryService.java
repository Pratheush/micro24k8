package com.mylearning.inventoryservice.service;

import com.mylearning.inventoryservice.dto.InventoryResponse;
import com.mylearning.inventoryservice.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    /*@Transactional(readOnly = true)
    public boolean isInStock(String skuCode) {

        return inventoryRepository.findBySkuCode(skuCode).isPresent();
    }*/

    @Transactional(readOnly = true)
    @SneakyThrows   // never use this SneakyThrows in production use Try-Catch instead
    public List<InventoryResponse> isInStock(List<String> skuCode) {
        // this Thread.sleep() is used to generate TimeOutException to use @TimeLimiter(name = UNSTABLE_PLACE_ORDER) which is implemented in OrderController.
        //log.info("InventoryService wait started");
        //Thread.sleep(10000);
        //log.info("InventoryService wait stopped");
        return inventoryRepository.findBySkuCodeIn(skuCode).stream()
                .map(inventory -> InventoryResponse.builder()
                        .skuCode(inventory.getSkuCode())
                        .isInStock(inventory.getQuantity()>0)
                        .build()
                ).collect(Collectors.toList());
    }
}
