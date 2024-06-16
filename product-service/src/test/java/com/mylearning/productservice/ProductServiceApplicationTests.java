package com.mylearning.productservice;

import com.mylearning.productservice.dto.ProductRequest;
import com.mylearning.productservice.model.Product;
import com.mylearning.productservice.repository.ProductRepository;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.HttpStatus;
import org.testcontainers.containers.MongoDBContainer;

import java.math.BigDecimal;
import java.util.Arrays;

import static org.hamcrest.MatcherAssert.assertThat;

//@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ProductServiceApplicationTests {

    @ServiceConnection
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:7.0.7");
    @LocalServerPort
    private Integer port;

    @Autowired
    private ProductRepository productRepository;
    @BeforeEach
    void setup() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;
        //RestAssured.defaultParser = Parser.TEXT;
    }

    @BeforeEach
    public void init() {
        productRepository.deleteAll();

        Product product1 = new Product("1", "Product 1", "Description 1", new BigDecimal("10.00"));
        Product product2 = new Product("2", "Product 2", "Description 2", new BigDecimal("20.00"));

        productRepository.saveAll(Arrays.asList(product1, product2));
    }

    static {
        mongoDBContainer.start();
    }

    @Test
    void shouldCreateProduct() throws Exception {

        ProductRequest productRequest = new ProductRequest("iPhone 13", "iPhone 13", BigDecimal.valueOf(1200));

        var responseBodyString = RestAssured.given()
                .contentType(ContentType.JSON)
                .body(productRequest)
                .when()
                .post("/api/product")
                .then()
                .log().all()
                .statusCode(201)
                .extract()
                .body().asString();

        assertThat(responseBodyString, Matchers.is("Product Created Successfully"));
    }


    @Test
    void getAllProducts_ShouldReturnAllProducts() {
        RestAssured.given()
                .contentType(ContentType.JSON)
                .when()
                .get("/api/product")
                .then()
                .log().all()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .body("$.size()", Matchers.is(2))
                .body("[0].id", Matchers.equalTo("1"))
                .body("[0].name", Matchers.equalTo("Product 1"))
                .body("[0].description", Matchers.equalTo("Description 1"))
                .body("[0].price", Matchers.equalTo(10.00f))
                .body("[1].id", Matchers.equalTo("2"))
                .body("[1].name", Matchers.equalTo("Product 2"))
                .body("[1].description", Matchers.equalTo("Description 2"))
                .body("[1].price", Matchers.equalTo(20.00f));
    }

}
