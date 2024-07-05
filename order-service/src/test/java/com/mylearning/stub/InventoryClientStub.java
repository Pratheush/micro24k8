package com.mylearning.stub;

import com.github.tomakehurst.wiremock.client.WireMock;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@UtilityClass
@Slf4j
public class InventoryClientStub {
    public static void stubInventoryCallTrue(String skuCode, Integer quantity) {
        log.info("Stubbing inventory call for skuCode: " + skuCode + " and quantity: " + quantity);
        WireMock.stubFor(WireMock.get(WireMock.urlEqualTo("/api/inventory?skuCode=" + skuCode + "&quantity=" + quantity))
                .willReturn(WireMock.aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("true")));
    }

    public static void stubInventoryCallFalse(String skuCode, Integer quantity) {
       log.info("Stubbing inventory call for skuCode: " + skuCode + " and quantity: " + quantity);
        WireMock.stubFor(WireMock.get(WireMock.urlEqualTo("/api/inventory?skuCode=" + skuCode + "&quantity=" + quantity))
                .willReturn(WireMock.aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("false")));
    }
}
