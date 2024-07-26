package com.mylearning.config;

import com.mylearning.client.InventoryClient;
import io.micrometer.observation.ObservationRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.ClientHttpRequestFactories;
import org.springframework.boot.web.client.ClientHttpRequestFactorySettings;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

import java.time.Duration;

@Configuration
@Slf4j
public class RestClientConfig {
    @Value("${inventory.url}")
    private String inventoryServiceUrl;

    // Injecting the ObservationRegistry and then provide this ObservationRegistry to the Rest-Client Builder
    // so in this way our Rest-Client will understand that we have to also propagate the Trace-Id along with Asynchronous Message to the Notification-Service
    // whenever Order-Service is making Rest-Client Synchronous calls to the Inventory-Service.
    // this is mandatory or else Trace-Id will not be propagated to the Inventory-Service properly.
    private final ObservationRegistry observationRegistry;

    public RestClientConfig(ObservationRegistry observationRegistry) {
        this.observationRegistry = observationRegistry;
    }

    @Bean
    public InventoryClient inventoryClient() {
        log.info("RestClientConfig.inventoryClient called");
        RestClient restClient = RestClient.builder()
                .baseUrl(inventoryServiceUrl)
                .requestFactory(getClientRequestFactory())
                .observationRegistry(observationRegistry)   // registering ObservationRegistry so that Trace-Id will always be send along with message to Notification-Service whenever Order-Service make RestClient Synchronous Calls to Inventory-Service.
                .build();
        var restClientAdapter = RestClientAdapter.create(restClient);
        var httpServiceProxyFactory = HttpServiceProxyFactory.builderFor(restClientAdapter).build();
        return httpServiceProxyFactory.createClient(InventoryClient.class); // its binding the InventoryClient interface to httpServiceProxyFactory
    }

    // Instead of adding @TimeLimiter annotation here we are adding time-out configuration
    private ClientHttpRequestFactory getClientRequestFactory() {
        log.info("RestClientConfig.getClientRequestFactory called");
        ClientHttpRequestFactorySettings clientHttpRequestFactorySettings = ClientHttpRequestFactorySettings.DEFAULTS
                .withConnectTimeout(Duration.ofSeconds(3))
                .withReadTimeout(Duration.ofSeconds(3));
        return ClientHttpRequestFactories.get(clientHttpRequestFactorySettings);
    }
}
