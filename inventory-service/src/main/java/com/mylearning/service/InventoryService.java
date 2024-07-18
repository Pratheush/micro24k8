package com.mylearning.service;

import com.mylearning.repository.InventoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    private final Logger log= LoggerFactory.getLogger(InventoryService.class);
    public InventoryService(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }


    @Transactional(readOnly = true)
    public Boolean isInStock(String skuCode, Integer quantity) throws InterruptedException {
        log.info("InventoryService isInStock :: skuCode: {}, quantity: {} ",skuCode, quantity);
        // this Thread.sleep() is used to generate TimeOutException to use @TimeLimiter(name = UNSTABLE_PLACE_ORDER) which is implemented in OrderController.
        log.info("InventoryService wait started");
        //Thread.sleep(1000);
        log.info("InventoryService wait stopped");
        return inventoryRepository.existsBySkuCodeAndQuantityIsGreaterThanEqual(skuCode, quantity)>0;
    }

}
