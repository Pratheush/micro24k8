package com.mylearning.orderservice;


import com.mylearning.orderservice.dto.OrderRequest;
import com.mylearning.orderservice.stub.InventoryStubs;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.HttpStatus;
import org.testcontainers.containers.MySQLContainer;

import java.math.BigDecimal;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 0)
class OrderServiceAppTest {

    @ServiceConnection
    static MySQLContainer mySQLContainer = new MySQLContainer("mysql:8.3.0");

    @LocalServerPort
    private Integer port;

    @BeforeEach
    void setup() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;
    }

    static {
        mySQLContainer.start();
    }

    @Test
    void shouldSubmitOrderWithTrueResponseFromInventoryUsingWiremock() {

        OrderRequest orderRequest= new OrderRequest(null,"LaptopBag", BigDecimal.valueOf(3000),9);

        InventoryStubs.stubInventoryCallTrue("LaptopBag", 9);

        var responseBodyString = given()
                .contentType(ContentType.JSON)
                .body(orderRequest)
                .when()
                .post("/api/order")
                .then()
                .log().all()
                .statusCode(HttpStatus.CREATED.value())
                .extract()
                .body().asString();

        assertThat(responseBodyString, Matchers.is("Order Placed Successfully"));

    }

    @Test
    void shouldSubmitOrderWithFalseResponseFromInventory() {

        OrderRequest orderRequest= new OrderRequest(null,"iphone_17", BigDecimal.valueOf(3000),3);

        InventoryStubs.stubInventoryCallFalse("iphone_17", 3);

        given()
                .contentType(ContentType.JSON)
                .body(orderRequest)
                .when()
                .post("/api/order")
                .then()
                .log().all()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body(Matchers.equalToIgnoringCase("Product with SkuCode : " + orderRequest.skuCode() + " is not in Stock"));

    }
}
