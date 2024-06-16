package com.mylearning.productservice.controller;


import com.mylearning.productservice.dto.ProductRequest;
import com.mylearning.productservice.dto.ProductResponse;
import com.mylearning.productservice.service.ProductService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/api/product")
public class ProductController {

    //@Autowired
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    //  sets the Content-Type header in the response.
    //  In your case, since you are returning a simple string message, you can set the content type to text/plain.
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public String createProduct(@RequestBody ProductRequest productRequest) {
        log.info("calling :: controller.createProduct >>>>> service.createProduct");
        productService.createProduct(productRequest);
        return "Product Created Successfully";
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public List<ProductResponse> getAllProducts() {
        log.info("calling :: controller.getAllProducts >>>>> service.getAllProducts");
        return productService.getAllProducts();
    }

}
