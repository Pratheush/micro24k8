package com.mylearning.config;

import com.mylearning.event.OrderPlacedEvent;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.HashMap;
import java.util.Map;

/*
    ProducerFactory: It’s the linchpin, ensuring Kafka’s compatibility with Spring.
    It necessitates certain properties, such as the Kafka broker’s address and the serialization classes.

    KafkaTemplate: Spring’s high-level abstraction over a Kafka producer, simplifying message-sending mechanics.

    kafkaTemplate.setObservationEnabled(true)
    This setting is particularly useful for collecting metrics, monitoring, and tracing the operations performed by the Kafka producer.

    If Micrometer is on the classpath and correctly configured, enabling observation on the KafkaTemplate will automatically start collecting metrics.
    Exporting Metrics: These metrics can be exported to various monitoring systems like Prometheus, Datadog, or any other supported monitoring tool.

    Enable Observation/Tracing: This kafkaTemplate.setObservationEnabled(true) statement enables observation features for the KafkaTemplate,
    which is responsible for sending messages to Kafka topics. When observation is enabled,
    it allows for the collection of metrics and tracing information related to the Kafka producer's activities.

    With tracing enabled, each message sent by the KafkaTemplate will include tracing information that can be picked up by distributed tracing systems.

    Trace Propagation: The tracing information usually includes trace IDs and span IDs, which are propagated with the messages,
     allowing for end-to-end tracing of message flow across different components of the system.

 */

@Configuration
public class KafkaProducerConfig {

    @Value("${spring.kafka.template.default-topic}")
    private String topicName;

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootServerUrl;

    @Value("${spring.kafka.producer.properties.schema.registry.url}")
    private String schemaRegistryUrl;
    @Bean
    public ProducerFactory<String, OrderPlacedEvent> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootServerUrl);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class);
        configProps.put("schema.registry.url", schemaRegistryUrl);
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    /*
        // setObservationEnabled(true): Enables observation/tracing for the KafkaTemplate,
        useful for metrics and Distributed tracing and Trace Propagation
        so TraceID will be propagated to Notification-Service as well,
         like same TraceID propagated to Inventory-Service from Order-Service.
     */
    @Bean
    public KafkaTemplate<String, OrderPlacedEvent> kafkaTemplate() {
        KafkaTemplate<String, OrderPlacedEvent> kafkaTemplate = new KafkaTemplate<>(producerFactory());
        kafkaTemplate.setObservationEnabled(true); // setObservationEnabled(true): Enables observation/tracing for the KafkaTemplate, useful for metrics and Distributed tracing and Trace Propagation so TraceID will be propagated to Notification-Service as well like same TraceID propagated to Inventory-Service from Order-Service.
        kafkaTemplate.setDefaultTopic(topicName);
        return kafkaTemplate;
    }

}
