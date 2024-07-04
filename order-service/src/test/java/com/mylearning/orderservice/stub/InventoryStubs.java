package com.mylearning.orderservice.stub;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;


@UtilityClass
@Slf4j
public class InventoryStubs {
    public static void stubInventoryCallTrue(String skuCode, Integer quantity) {
        log.info("InventoryStubs stubInventoryCallTrue skuCode:{} quantity:{}", skuCode, quantity);
        // stubs the inventory service to return a 200 response with a body of "true".
        stubFor(get(urlEqualTo("/api/inventory?skuCode=" + skuCode + "&quantity=" + quantity))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("true")));
        log.info("InventoryStubs stubInventoryCallTrue ended");
    }

    public static void stubInventoryCallFalse(String skuCode, Integer quantity) {
        log.info("InventoryStubs stubInventoryCallFalse skuCode:{} quantity:{}", skuCode, quantity);
        // stubs the inventory service to return a 200 response with a body of "false".
        stubFor(get(urlEqualTo("/api/inventory?skuCode=" + skuCode + "&quantity=" + quantity))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("false")));
        log.info("InventoryStubs stubInventoryCallFalse ended");
    }
}
