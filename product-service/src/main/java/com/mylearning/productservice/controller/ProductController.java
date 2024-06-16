package com.mylearning.productservice.controller;


import com.mylearning.productservice.dto.ProductRequest;
import com.mylearning.productservice.dto.ProductResponse;
import com.mylearning.productservice.model.Product;
import com.mylearning.productservice.service.ProductService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/api/product")
@RequiredArgsConstructor
public class ProductController {

    //@Autowired
    private ProductService productService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createProduct(@RequestBody ProductRequest productRequest) {
        log.info("calling :: controller.createProduct >>>>> service.createProduct");
        productService.createProduct(productRequest);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ProductResponse> getAllProducts() {
        log.info("calling :: controller.getAllProducts >>>>> service.getAllProducts");
        return productService.getAllProducts();
    }

}
