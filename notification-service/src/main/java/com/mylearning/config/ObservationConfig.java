package com.mylearning.config;

import com.mylearning.event.OrderPlacedEvent;
import io.micrometer.observation.ObservationRegistry;
import io.micrometer.observation.aop.ObservedAspect;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;

/*
    we need to enable observation for Kafka for that we need to add additional configuration we need to enable observation inside Kafka-Listener
    we need to autowire ConcurrentKafkaListenerContainerFactory Bean then we have to setObservationEnabled property as true if we don't add this
    then there will be no trace-id for the messages that are send through kafka . if we want this message to be present in all the requests
    so when a request coming from Order-Service to Notification-Service by adding this configuration the Trace-ID will also be propagated.
    likewise in Order-Service in RestClientConfig we need to we need to add some additional changes for the Rest-Client. so while defining the InventoryClient (RestClient) Bean
    in Order-Service we have to inject the ObservationRegistry then provide this ObservationRegistry to the Rest-Client Builder so in this way our Rest-Client will understand
    that we have to also propagate the Trace-Id whenever we are making Rest-Client Synchronous calls to the Inventory-Service.

 */

@Configuration
public class ObservationConfig {

    /*private final ConcurrentKafkaListenerContainerFactory<String, OrderPlacedEvent> concurrentKafkaListenerContainerFactory;

    public ObservationConfig(ConcurrentKafkaListenerContainerFactory<String, OrderPlacedEvent> concurrentKafkaListenerContainerFactory) {
        this.concurrentKafkaListenerContainerFactory = concurrentKafkaListenerContainerFactory;
    }*/

    /*@Bean
    public ConcurrentKafkaListenerContainerFactory<String, OrderPlacedEvent> kafkaListenerContainerFactory(
            ConsumerFactory<String, OrderPlacedEvent> consumerFactory) {
        ConcurrentKafkaListenerContainerFactory<String, OrderPlacedEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        return factory;
    }
*/
    /*@PostConstruct
    public void setObserverationForKafkaTemplate(){
        concurrentKafkaListenerContainerFactory.getContainerProperties().setObservationEnabled(true);
    }*/
    @Bean
    ObservedAspect observedAspect(ObservationRegistry registry) {
        return new ObservedAspect(registry);
    }
}