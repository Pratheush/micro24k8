package com.mylearning.orderservice;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.matching.StringValuePattern;
import com.mylearning.orderservice.client.InventoryClient;
import com.mylearning.orderservice.dto.OrderRequest;
import com.mylearning.stub.InventoryClientStub;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import lombok.extern.slf4j.Slf4j;
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
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.utility.DockerImageName;

import java.math.BigDecimal;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;

//@ActiveProfiles(value = {"qa"})
@Slf4j
@ExtendWith(MockitoExtension.class)
//@RunWith(SpringRunner.class)
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
    }

    // Configuring the rest-assured
    @BeforeEach
    void init() {
        // defining the base URI for our application
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;
    }

    @BeforeAll
    public static void mysqlSetup() {
        mySQLContainer.start();
    }

    @AfterAll
    public static void mysqlTeardown() {
        mySQLContainer.stop();
    }


    /*static {
        mySQLContainer.start();
    }*/


    @Test
    void shouldPlaceOrder() {
        String requestBody = """
         {
             "skuCode":"iphone_15",
             "price": 1000,
             "quantity": 1
         }
         """;


        InventoryClientStub.stubInventoryCallTrue("iphone_15", 1);


        var responseBodyString = RestAssured.given()
                .contentType("application/json")
                .body(requestBody)
                .when()
                .post("/api/order")
                .then()
                .statusCode(201)
                .extract()
                .body().asString();
        assertThat(responseBodyString, Matchers.is("Order placed successfully"));
    }

    /**
     * This class demonstrates the usage of WireMock, a library for stubbing and mocking HTTP-based services.
     * It starts a WireMock server, configures it to respond with JSON for a specific URL, and stops the server
     * when the user presses enter.
     */
    @Test
    void shouldSubmitOrderWithTrueResponseFromInventory() {

        // below WireMock.configureFor is commented since WireMock is @AutoConfigureWireMock(port=0) means port is random
        WireMock.configureFor("localhost",7070);

        StringValuePattern stringValuePatternSkuCode=WireMock.equalTo("\"iphone_17\"");

        /*WireMock.stubFor(WireMock.get(WireMock.urlPathEqualTo("/api/inventory"))
                .withQueryParam("skuCode", WireMock.matching("[a-zA-Z0-9_]+"))
                .withQueryParam("quantity",WireMock.matching("\\d+"))
                .withHeader("Content-Type", WireMock.equalTo("application/json"))
                .willReturn(WireMock.aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type","text/plain")
                        .withBody("true")));*/

        // Stub for the /api/inventory endpoint with specific query params
        WireMock.stubFor(WireMock.get(WireMock.urlPathEqualTo("/api/inventory"))
                .withQueryParam("skuCode", WireMock.equalTo("iphone_17"))
                .withQueryParam("quantity", WireMock.equalTo("3"))
                .willReturn(WireMock.aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("true")));

        String submitOrderJson = """
                {
                     "skuCode": "iphone_17",
                     "price": 1000,
                     "quantity": 3
                }
                """;

        // Prepare the JSON request body
        String jsonBody = "{ \"skuCode\": \"iphone_17\", \"price\": 1000, \"quantity\": 3 }";

        OrderRequest orderRequest= new OrderRequest(null,"iphone_17", BigDecimal.valueOf(1000),3);

        log.info("Rest Assured Attributes::uri::{} port ::{}",RestAssured.baseURI,RestAssured.port);

        //WireMock.verify(WireMock.getRequestedFor(WireMock.urlEqualTo("/api/inventory?skuCode=iphone_17&quantity=3")));

        // uses Rest-Assured to simulate a POST request to the /api/order endpoint.
       // var responseBodyString =
                RestAssured.given()
                .contentType(ContentType.JSON)
                .body(jsonBody)
                .when()
                .post("/api/order")
                .then()
                .log().all()
                .statusCode(HttpStatus.CREATED.value())
                .body(Matchers.equalTo("Order Placed Successfully"));
                //.extract()
                //.body().asString();

        //assertThat(responseBodyString, Matchers.is("Order Placed Successfully"));

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
                //.body("message",Matchers.equalTo("Product with SkuCode :" + orderRequest.skuCode() + " is not in Stock"));
                .body(Matchers.equalTo("Product with SkuCode :" + orderRequest.skuCode() + " is not in Stock"));
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

        WireMock.configureFor("localhost",7070);

        StringValuePattern stringValuePatternSkuCode=WireMock.equalTo("\"iphone_17\"");

        /*WireMock.stubFor(WireMock.get(WireMock.urlPathEqualTo("/api/inventory"))
                .withQueryParam("skuCode", WireMock.matching("[a-zA-Z0-9_]+"))
                .withQueryParam("quantity",WireMock.matching("\\d+"))
                .withHeader("Content-Type", WireMock.equalTo("application/json"))
                .willReturn(WireMock.aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type","application/json")
                        .withBody("false")));*/

        /*String skuCode = "iphone_17";
        Integer quantity= 3;
        WireMock.stubFor(WireMock.get(WireMock.urlPathEqualTo("/api/inventory?skuCode=" + skuCode + "&quantity=" + quantity))
                .withHeader("Content-Type", WireMock.equalTo("application/json"))
                .willReturn(WireMock.aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type","application/json")
                        .withBody("false")));*/

        // Stub for the /api/inventory endpoint with specific query params
        WireMock.stubFor(WireMock.get(WireMock.urlPathEqualTo("/api/inventory"))
                .withQueryParam("skuCode", WireMock.equalTo("iphone_17"))
                .withQueryParam("quantity", WireMock.equalTo("3"))
                .willReturn(WireMock.aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("false")));

        // Prepare the JSON request body
        String jsonBody = "{ \"skuCode\": \"iphone_17\", \"price\": 1000, \"quantity\": 3 }";

        OrderRequest orderRequest= new OrderRequest(null,"iphone_17", BigDecimal.valueOf(1000),3);

        //InventoryClientStub.stubInventoryCallFalse(orderRequest.skuCode(),orderRequest.quantity());

        log.info("Rest Assured Attributes::uri::{} port ::{}",RestAssured.baseURI,RestAssured.port);

       given()
                .contentType(ContentType.JSON)
                .body(orderRequest)
                .when()
                .post("/api/order")
                .then()
                .log().all()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body(Matchers.equalToIgnoringCase("Product with SkuCode :" + orderRequest.skuCode() + " is not in Stock"));

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
