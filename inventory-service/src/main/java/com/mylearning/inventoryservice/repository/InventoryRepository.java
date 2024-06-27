package com.mylearning.inventoryservice.repository;

import com.mylearning.inventoryservice.dto.InventoryResponse;
import com.mylearning.inventoryservice.model.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    Optional<Inventory> findBySkuCode(String skuCode);

    List<Inventory> findBySkuCodeIn(List<String> skuCode);

    /*@Query(value = "SELECT COUNT(*) > 0 FROM t_inventory WHERE sku_code = :skuCode AND quantity >= :quantity LIMIT 1", nativeQuery = true)
    Integer existsBySkuCodeAndQuantityIsGreaterThanEqual(String skuCode, int quantity);*/

    /*@Query(value = "SELECT * FROM products WHERE sku_code IN :skuCodes AND quantity IN :quantities", nativeQuery = true)
    List<Product> findProductsBySkuCodeAndQuantity(@Param("skuCodes") List<String> skuCodes, @Param("quantities") List<Integer> quantities);*/

    /*@Query(value = "SELECT COUNT(*) > 0 FROM t_inventory WHERE sku_code IN ?1 AND quantity IN ?2 LIMIT 1", nativeQuery = true)
    Integer existsBySkuCodeAndQuantityIsGreaterThanEqual(List<String> skuCodeList, List<Integer> quantityList);*/

    /*@Query(value = "SELECT COUNT(*) > 0 FROM t_inventory WHERE sku_code IN :skuCodes AND age BETWEEN :minAge AND :maxAge", nativeQuery = true)
    List<Inventory> findProductsBySkuCodeAndAgeRange(@Param("skuCodes") List<String> skuCodes, @Param("minAge") int minAge, @Param("maxAge") int maxAge);*/

    @Query(value = "SELECT COUNT(*) > 0 FROM t_inventory WHERE sku_code = :skuCode AND quantity >= :quantity", nativeQuery = true)
    Long existsBySkuCodeAndQuantityIsGreaterThanEqual(String skuCode, int quantity);



}
