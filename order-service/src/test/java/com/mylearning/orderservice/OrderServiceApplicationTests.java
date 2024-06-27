package com.mylearning.orderservice;

import com.mylearning.orderservice.client.InventoryClient;
import com.mylearning.orderservice.dto.OrderRequest;
import com.mylearning.orderservice.exception.ProductNotFoundException;
import com.mylearning.orderservice.stub.InventoryStubs;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.MySQLContainer;

import java.math.BigDecimal;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;

//@ActiveProfiles(value = {"qa"})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 0)
class OrderServiceApplicationTests {

    @ServiceConnection
    static MySQLContainer mySQLContainer = new MySQLContainer("mysql:8.3.0");

    @LocalServerPort
    private Integer port;

    @MockBean
    private InventoryClient inventoryClient;

    @BeforeEach
    void setup() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;
    }

    static {
        mySQLContainer.start();
    }

    @Test
    void shouldSubmitOrderWithTrueResponseFromInventory() {

        OrderRequest orderRequest= new OrderRequest(null,"Laptop-Bag", BigDecimal.valueOf(3000),9);

        InventoryStubs.stubInventoryCallTrue("Laptop-Bag", 9);

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
    public void placeOrder_ProductNotInStock_ShouldReturnBadRequest() {
        // Mock the InventoryClient to return false when isInStock is called
        Mockito.when(inventoryClient.isInStock(Mockito.anyString(),Mockito.anyInt())).thenReturn(false);

        // Create the OrderRequest payload
        OrderRequest orderRequest= new OrderRequest(1L,"iphone_3", BigDecimal.valueOf(3000),3);

        // Send the POST request and verify the response
        given()
                .contentType(ContentType.JSON)
                .body(orderRequest)
                .when()
                .post("/api/order")
                .then()
                .log().all()
                .statusCode(HttpStatus.NOT_FOUND.value())
                //.body("message",Matchers.equalTo("Product with SkuCode : " + orderRequest.skuCode() + " is not in Stock"));
                .body("error",Matchers.equalTo("Not Found"));
    }

    @Test
    void shouldSubmitOrderWithFalseResponseFromInventory() {

        OrderRequest orderRequest= new OrderRequest(null,"iphone_17", BigDecimal.valueOf(3000),3);

        InventoryStubs.stubInventoryCallFalse("iphone_17", 3);

        var productNotFoundException = given()
                .contentType(ContentType.JSON)
                .body(orderRequest)
                .when()
                .post("/api/order")
                .then()
                .log().all()
                .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .extract().body().as(ProductNotFoundException.class);


        assertThat(productNotFoundException, Matchers.instanceOf(ProductNotFoundException.class));

    }

    @Test
    void placeOrder_ShouldReturnSuccessMessage() {

        OrderRequest orderRequest = new OrderRequest(null, "SKU123", new BigDecimal("10.00"), 1);

        given()
                .contentType(ContentType.JSON)
                .body(orderRequest)
                .when()
                .post("/api/order")
                .then()
                .log().all()
                .statusCode(HttpStatus.CREATED.value())
                .body(Matchers.equalTo("Order Placed Successfully"));
    }
}
