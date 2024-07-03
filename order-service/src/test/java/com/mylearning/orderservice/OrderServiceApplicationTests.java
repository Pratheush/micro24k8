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
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.HttpStatus;
import org.testcontainers.containers.MySQLContainer;

import java.math.BigDecimal;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;

//@ActiveProfiles(value = {"qa"})
@ExtendWith(MockitoExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 0) // i want to autoconfigure the wiremock related classes for testing. will start wiremock server as part of the spring-application context
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
    void shouldSubmitOrder() {
        String submitOrderJson = """
                {
                     "skuCode": "pixel_8",
                     "price": 1000,
                     "quantity": 100
                }
                """;

        OrderRequest orderRequest= new OrderRequest(null,"pixel_8", BigDecimal.valueOf(1000),10);

        var responseBodyString = RestAssured.given()
                .contentType("application/json")
                .body(orderRequest)
                .when()
                .post("/api/order")
                .then()
                .log().all()
                .statusCode(201)
                .extract()
                .body().asString();

        assertThat(responseBodyString, Matchers.is("Order Placed Successfully"));
    }

    // stubs are programs or routines that simulate the behavior of software components or modules.
    // When a particular component/module is missing or still under development, stubs step in to
    // temporarily replace these yet-to-be-developed components.

    // wiremock will stub the call to Inventory-Service
    //  a library called Wiremock that provides a mock server environment to test our Order Service
    //  by making some mock HTTP calls. By using Wiremock, we can verify if our Order Service is calling
    //  the inventory service with correct URL Params/Request Body/ Path Variables or not.
    //  We can also stub the response and test how our service is responding for various scenarios.
    //  spring-cloud-starter-contract-stub-runner wiremock dependency.
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
    void placeOrder_ProductNotInStock_ShouldReturnBadRequest() {
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
                .body(Matchers.equalTo("Product with SkuCode : " + orderRequest.skuCode() + " is not in Stock"));
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
