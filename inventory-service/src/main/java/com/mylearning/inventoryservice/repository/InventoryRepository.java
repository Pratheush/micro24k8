package com.mylearning.inventoryservice.repository;

import com.mylearning.inventoryservice.dto.InventoryResponse;
import com.mylearning.inventoryservice.model.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    Optional<Inventory> findBySkuCode(String skuCode);

    List<Inventory> findBySkuCodeIn(List<String> skuCode);

    @Query(value = "SELECT COUNT(*) > 0 FROM t_inventory WHERE sku_code = :skuCode AND quantity >= :quantity LIMIT 1", nativeQuery = true)
    Integer existsBySkuCodeAndQuantityIsGreaterThanEqual(String skuCode, int quantity);
}
