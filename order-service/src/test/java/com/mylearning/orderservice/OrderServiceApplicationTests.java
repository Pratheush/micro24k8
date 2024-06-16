package com.mylearning.orderservice;

import com.mylearning.orderservice.dto.OrderLineItemsDto;
import com.mylearning.orderservice.dto.OrderRequest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.HttpStatus;
import org.testcontainers.containers.MySQLContainer;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class OrderServiceApplicationTests {

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
    void shouldSubmitOrder() {

        OrderLineItemsDto orderLineItemsDto=new OrderLineItemsDto(null,"Laptop Bag", BigDecimal.valueOf(3000),9);
        OrderLineItemsDto orderLineItemsDto1=new OrderLineItemsDto(null,"Lugage Bag", BigDecimal.valueOf(5000),13);
        OrderRequest orderRequest= new OrderRequest(List.of(orderLineItemsDto, orderLineItemsDto1));

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
    void placeOrder_ShouldReturnSuccessMessage() {
        OrderLineItemsDto orderLineItem1 = new OrderLineItemsDto(null, "SKU123", new BigDecimal("10.00"), 1);
        OrderLineItemsDto orderLineItem2 = new OrderLineItemsDto(null, "SKU456", new BigDecimal("20.00"), 2);
        OrderRequest orderRequest = new OrderRequest(Arrays.asList(orderLineItem1, orderLineItem2));

        given()
                .contentType(ContentType.JSON)
                .body(orderRequest)
                .when()
                .post("/api/order")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .body(Matchers.equalTo("Order Placed Successfully"));
    }
}
