package com.mylearning.productservice.service;

import com.mylearning.productservice.dto.ProductRequest;
import com.mylearning.productservice.dto.ProductResponse;
import com.mylearning.productservice.model.Product;
import com.mylearning.productservice.repository.ProductRepository;
import com.mylearning.productservice.util.AppUtils;
import lombok.RequiredArgsConstructor;
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
        logger.info("Creating Product Product Request:: {}", productRequest);
        Product product= AppUtils.dto2modelpro(productRequest);
        logger.info("After AppUtils :: {}",product);
        productRepository.save(product);
        logger.info("Product {} :: {} is saved", product.getId(),product.getName());
    }

    public List<ProductResponse> getAllProducts() {
        List<Product> products = productRepository.findAll();
        logger.info("getAllProducts called >> list of products:: {}", products);
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
