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
        log.info("InventoryStubs stub True:{}", skuCode);
        stubFor(get(urlEqualTo("/api/inventory?skuCode=" + skuCode + "&quantity=" + quantity))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("true")));
        log.info("InventoryStubs stub True method_end :{}", skuCode);
    }

    public void stubInventoryCallFalse(String skuCode, Integer quantity) {
        log.info("InventoryStubs stub False:{}", skuCode);
        stubFor(get(urlEqualTo("/api/inventory?skuCode=" + skuCode + "&quantity=" + quantity))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("false")));
        log.info("InventoryStubs stub False method_end :{}", skuCode);
    }
}
