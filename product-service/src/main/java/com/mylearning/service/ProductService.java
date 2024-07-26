package com.mylearning.service;

import com.mylearning.dto.ProductRequest;
import com.mylearning.dto.ProductResponse;
import com.mylearning.model.Product;
import com.mylearning.repository.ProductRepository;
import com.mylearning.util.AppUtils;
import io.micrometer.observation.annotation.Observed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service

public class ProductService {
    //@Autowired
    private final ProductRepository productRepository;

    private final Logger logger= LoggerFactory.getLogger(ProductService.class);

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public void createProduct(ProductRequest productRequest) {
        logger.info("ProductService Creating Product Product Request:: {}", productRequest);
        Product product= AppUtils.dto2modelpro(productRequest);
        logger.info("After AppUtils :: {}",product);
        productRepository.save(product);
        logger.info("Product {} :: {} is saved", product.getId(),product.getName());
    }

    // @Observed annotation tells i want to track the execution time for this method
    // if we want to track the execution time of all the methods of the class then define @Observed annotation at class level not on the method level
    // to see product-service actuator metrics then http://localhost:8086/actuator/metrics we will see all @Observed annotated methods
    // to check specific actuator metrics then just run command like this http://localhost:8086/actuator/metrics/get.products in the browser
    // using Observability we can publish our custom metrics :: here get.products is my custom metrics
    @Observed(name = "get.products",lowCardinalityKeyValues = {"author","Raj R"},contextualName = "product-service.find-all")
    public List<ProductResponse> getAllProducts() {
        List<Product> products = productRepository.findAll();
        logger.info("ProductService getAllProducts called >> list of products:: {}", products);
        return products.stream().map(this::mapToProductResponse).toList();
    }

    private ProductResponse mapToProductResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .build();
    }



}
