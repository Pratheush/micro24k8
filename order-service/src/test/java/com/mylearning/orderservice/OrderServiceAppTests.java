package com.mylearning.orderservice;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.mylearning.orderservice.dto.OrderRequest;
import com.mylearning.stub.InventoryClientStub;
import io.restassured.RestAssured;
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

import static org.hamcrest.MatcherAssert.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 0)
class OrderServiceAppTests {

    @ServiceConnection
    static MySQLContainer mySQLContainer = new MySQLContainer("mysql:8.3.0");

    @LocalServerPort
    private Integer port;

    @BeforeEach
    void setup(){
        RestAssured.baseURI="http://localhost";
        RestAssured.port=port;
    }

    static {
        mySQLContainer.start();
    }

    @Test
    void shouldPlaceOrder() {
        String requestBody = """
         {
             "skuCode":"iphone_15",
             "price": 1000,
             "quantity": 1
         }
         """;

        OrderRequest orderRequest= new OrderRequest(null,"iphone_15", BigDecimal.valueOf(1000),1);

        WireMock.stubFor(WireMock.get(WireMock.urlPathEqualTo("/api/inventory"))
                .withQueryParam("skuCode", WireMock.matching("[a-zA-Z0-9_]+"))
                .withQueryParam("quantity",WireMock.matching("\\d+"))
                .willReturn(WireMock.aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type","application/json")
                        .withBody("true")));

        //InventoryClientStub.stubInventoryCallTrue("iphone_15", 1);


        var responseBodyString = RestAssured.given()
                .contentType("application/json")
                .body(orderRequest)
                .when()
                .post("/api/order")
                .then()
                .statusCode(201)
                .extract()
                .body().asString();

        WireMock.verify(WireMock.getRequestedFor(WireMock.urlEqualTo("/api/inventory?skuCode=iphone_15&quantity=1")));
        assertThat(responseBodyString, Matchers.is("Order Placed Successfully"));
    }

    @Test
    void productNotFound() {
        String requestBody = """
         {
             "skuCode":"iphone_15",
             "price": 1000,
             "quantity": 1
         }
         """;
        var skuCode="iphone_15";
        InventoryClientStub.stubInventoryCallFalse("iphone_15", 1);


        RestAssured.given()
                .contentType("application/json")
                .body(requestBody)
                .when()
                .post("/api/order")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body(Matchers.equalToIgnoringCase("Product with SkuCode :" + skuCode + " is not in Stock"));

        WireMock.verify(WireMock.getRequestedFor(WireMock.urlEqualTo("/api/inventory?skuCode=iphone_15&quantity=1")));

    }
}
