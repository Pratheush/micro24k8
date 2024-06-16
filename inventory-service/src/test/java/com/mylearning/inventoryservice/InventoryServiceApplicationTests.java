package com.mylearning.inventoryservice;

import com.mylearning.inventoryservice.model.Inventory;
import com.mylearning.inventoryservice.repository.InventoryRepository;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.HttpStatus;
import org.testcontainers.containers.MySQLContainer;

import java.math.BigDecimal;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class InventoryServiceApplicationTests {

    @Autowired
    private InventoryRepository inventoryRepository;

    @ServiceConnection
    static MySQLContainer mySQLContainer = new MySQLContainer("mysql:8.3.0");
    @LocalServerPort
    private Integer port;

    @BeforeEach
    void setup() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;
    }

    @BeforeEach
    public void init() {
        inventoryRepository.deleteAll();

        Inventory inventory1 = new Inventory(1L, "Inventory1", 5);
        Inventory inventory2 = new Inventory(2L, "Inventory2", 10);

        inventoryRepository.saveAll(Arrays.asList(inventory1, inventory2));
    }

    static {
        mySQLContainer.start();
    }

    @Test
    void shouldReadInventory() {
        var response = RestAssured.given()
                .when()
                .get("/api/inventory?skuCode=Inventory1&quantity=1")
                .then()
                .log().all()
                .statusCode(200)
                .extract().response().as(Boolean.class);
        assertTrue(response);

        var negativeResponse = RestAssured.given()
                .when()
                .get("/api/inventory?skuCode=Inventory2&quantity=11")
                .then()
                .log().all()
                .statusCode(HttpStatus.OK.value())
                .extract().response().as(Boolean.class);
        assertFalse(negativeResponse);

    }

}
