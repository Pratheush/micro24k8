package com.mylearning.orderservice;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.mylearning.orderservice.client.InventoryClient;
import com.mylearning.orderservice.dto.OrderRequest;
import com.mylearning.orderservice.stub.InventoryStubs;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
//import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.utility.DockerImageName;

import java.math.BigDecimal;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;

//@ActiveProfiles(value = {"qa"})
@ExtendWith(MockitoExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@AutoConfigureWireMock(port = 0)
class OrderServiceApplicationTests {

    // this will dynamically assign host and port to the test container
    // @ServiceConnection removes the need to write @DynamicPropertySource property overrides.
    @ServiceConnection
    static MySQLContainer mySQLContainer = new MySQLContainer(DockerImageName.parse("mysql:8.3.0"));
        /*    .withUsername("testUser")
      .withPassword("testSecret")
      .withDatabaseName("testDatabase");*/

    /*@DynamicPropertySource
    static void datasourceProperties(DynamicPropertyRegistry registry) {

        registry.add("spring.datasource.url", mySQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", mySQLContainer::getUsername);
        registry.add("spring.datasource.password", mySQLContainer::getPassword);
        registry.add("spring.datasource.driver-class-name", mySQLContainer::getDriverClassName);
    }*/

    // this annotation will do whenever the application is running it will inject the port number
    @LocalServerPort
    private Integer port;

    @MockBean
    private InventoryClient inventoryClient;

    private static WireMockServer wireMockServer;

    @BeforeAll
    public static void setup() {
        // Create a WireMock server instance
        wireMockServer = new WireMockServer(WireMockConfiguration.wireMockConfig().port(7070));
        // Start the WireMock server
        wireMockServer.start();
    }

    @AfterAll
    public static void teardown() {
        // Stop the WireMock server
        wireMockServer.stop();
        mySQLContainer.stop();
    }

    // Configuring the rest-assured
    @BeforeEach
    void init() {
        // defining the base URI for our application
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;
    }

    static {
        mySQLContainer.start();
    }

    /**
     * This class demonstrates the usage of WireMock, a library for stubbing and mocking HTTP-based services.
     * It starts a WireMock server, configures it to respond with JSON for a specific URL, and stops the server
     * when the user presses enter.
     */
    @Test
    void shouldSubmitOrderWithTrueResponseFromInventory() {

        String submitOrderJson = """
                {
                     "skuCode": "iphone_15",
                     "price": 1000,
                     "quantity": 100
                }
                """;

        OrderRequest orderRequest= new OrderRequest(null,"iphone_15", BigDecimal.valueOf(1000),100);

        InventoryStubs.stubInventoryCallTrue("iphone_15", 100);

        // uses Rest-Assured to simulate a POST request to the /api/order endpoint.
        var responseBodyString = RestAssured.given()
                .contentType(ContentType.JSON)
                .body(submitOrderJson)
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
    void placeOrder_ProductNotInStock_UsingMockito() {
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
    void placeOrder_ProductInStock_UsingMockito() {
        // Mock the InventoryClient to return false when isInStock is called
        Mockito.when(inventoryClient.isInStock(Mockito.anyString(),Mockito.anyInt())).thenReturn(true);

        // Create the OrderRequest payload
        OrderRequest orderRequest= new OrderRequest(1L,"iphone_3", BigDecimal.valueOf(3000),3);

        // Send the POST request and verify the response
        var responseBodyString=given()
                .contentType(ContentType.JSON)
                .body(orderRequest)
                .when()
                .post("/api/order")
                .then()
                .log().all()
                .statusCode(HttpStatus.CREATED.value())
                        .extract().body().asString();

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
