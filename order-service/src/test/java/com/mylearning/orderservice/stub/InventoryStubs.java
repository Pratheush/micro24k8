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
    public void stubInventoryCallTrue(String skuCode, Integer quantity) {
        log.info("InventoryStubs stubInventoryCallTrue skuCode:{} quantity:{}", skuCode, quantity);
        stubFor(get(urlEqualTo("/api/inventory?skuCode=" + skuCode + "&quantity=" + quantity))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("true")));
    }

    public void stubInventoryCallFalse(String skuCode, Integer quantity) {
        log.info("InventoryStubs stubInventoryCallFalse skuCode:{} quantity:{}", skuCode, quantity);
        stubFor(get(urlEqualTo("/api/inventory?skuCode=" + skuCode + "&quantity=" + quantity))
                .willReturn(aResponse()
                        .withStatus(404)
                        .withHeader("Content-Type", "application/json")
                        .withBody("false")));
    }
}
