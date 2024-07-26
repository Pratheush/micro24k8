package com.mylearning.config;

import com.mylearning.event.OrderPlacedEvent;
import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;

import java.util.HashMap;
import java.util.Map;

/*
    ConsumerFactory: It sets the stage for Kafka’s compatibility with Spring and mandates certain properties
    such as the Kafka broker’s address, the consumer group ID, and the deserialization classes.

    ConcurrentKafkaListenerContainerFactory:
    This factory aids in creating containers for methods annotated with @KafkaListener

    concurrentKafkaListenerContainerFactory.getContainerProperties().setObservationEnabled(true); is used to enable observation features for Kafka listener containers in a Spring application.
    This setting is part of configuring a ConcurrentKafkaListenerContainerFactory bean, which is responsible for creating Kafka listener containers that handle incoming Kafka messages.

    ConcurrentKafkaListenerContainerFactory is a Spring bean used to create and configure Kafka listener containers.
    The ConcurrentKafkaListenerContainerFactory is used to configure Kafka listener containers that handle incoming messages.

    do not set concurrentKafkaListenerContainerFactory.getContainerProperties().setObservationEnabled(true); on the Kafka producer side.
    This specific configuration is meant for the Kafka consumer side to enable observation and monitoring of the Kafka listener containers.
    To summarize, the setObservationEnabled(true) configuration is specifically for Kafka consumers to enhance observability and should not be applied to Kafka producers.
 */

@Configuration
public class KafkaConsumerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${spring.kafka.consumer.group-id}")
    private String groupId;

    @Value("${spring.kafka.consumer.properties.schema.registry.url}")
    private String schemaRegistryUrl;

    @Bean
    public Map<String, Object> consumerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        props.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, KafkaAvroDeserializer.class);
        props.put("specific.avro.reader", true);
        props.put("schema.registry.url", schemaRegistryUrl);
        //props.put("spring.deserializer.key.delegate.class",StringDeserializer.class);
        //props.put("spring.deserializer.value.delegate.class", KafkaAvroDeserializerConfig.class.getName());
        return props;
    }

    @Bean
    public ConsumerFactory<String, OrderPlacedEvent> consumerFactory() {
        return new DefaultKafkaConsumerFactory<>(consumerConfigs());
    }

    // kafkaListenerContainerFactory
    // concurrentKafkaListenerContainerFactory
    /*
        factory.getContainerProperties().setObservationEnabled(true);: Enables observation on the container properties.
        Observation might refer to enabling metrics or monitoring features, allowing for better insight into the Kafka listener's performance and behavior.

        By calling setObservationEnabled(true), you're enabling additional observation capabilities for the Kafka listener containers.
        This might include metrics collection, logging, or tracing, depending on what the observation feature supports in your Spring Kafka setup.

        USE CASE ::
        Enabling observation is useful for monitoring and managing Kafka listener containers, especially in production environments.
        It helps in tracking message consumption, diagnosing issues, and ensuring that the system operates as expected.

        -----

        This method defines a Spring bean named kafkaListenerContainerFactory which creates and configures Kafka listener containers.
        Overall, this configuration enhances the observability of your Kafka consumers, making it easier to monitor their performance and troubleshoot any issues that arise

        ConcurrentKafkaListenerContainerFactory
        Type: Class
        Role: A class provided by the Spring Kafka framework used to create and configure Kafka listener containers that can consume messages concurrently.
        Usage: You configure it to define how Kafka listeners should behave, including setting properties such as the consumer factory, concurrency, and other container properties.

        kafkaListenerContainerFactory
        Type: Bean Name
        Role: The name of a Spring bean that returns an instance of ConcurrentKafkaListenerContainerFactory
        Usage: This is a bean defined in your Spring configuration that provides the configuration for Kafka listeners.
               It is typically used as a reference in the @KafkaListener annotation to specify the container factory that should be used for creating Kafka listener containers.

     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, OrderPlacedEvent>  kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, OrderPlacedEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        factory.getContainerProperties().setObservationEnabled(true);
        return factory;
    }

}

