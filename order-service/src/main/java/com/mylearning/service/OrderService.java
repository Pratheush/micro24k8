package com.mylearning.service;


import com.mylearning.client.InventoryClient;
import com.mylearning.dto.OrderRequest;
import com.mylearning.event.OrderPlacedEvent;
import com.mylearning.exception.ProductNotFoundException;
import com.mylearning.model.Order;
import com.mylearning.repository.OrderRepository;

import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
@Transactional
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;

    private final InventoryClient inventoryClient;

    private final KafkaTemplate<String, OrderPlacedEvent> kafkaTemplate;

    //@Autowired
    private final ObservationRegistry observationRegistry;

    private final Tracer tracer;

    @Value("${spring.kafka.template.default-topic}")
    private String topicName;

    @Autowired
    public OrderService(OrderRepository orderRepository, InventoryClient inventoryClient, KafkaTemplate<String, OrderPlacedEvent> kafkaTemplate, Tracer tracer, ObservationRegistry observationRegistry, Tracer tracer1) {
        this.orderRepository = orderRepository;
        this.inventoryClient=inventoryClient;
        this.kafkaTemplate = kafkaTemplate;
        this.observationRegistry = observationRegistry;
        this.tracer = tracer;
    }

    // if we use CompletableFuture<Boolean> as return type for InventoryClient abstract method then use below method
    /*public void placeOrder(OrderRequest orderRequest) {
        CompletableFuture<Boolean> inStockFuture = inventoryClient.isInStock(orderRequest.skuCode(), orderRequest.quantity());

        // thenAccept is used to handle the result of the CompletableFuture.
        inStockFuture.thenAccept(inStock ->{
            log.info("Order-Service placeOrder -- inStock:: {}", inStock);
            if (inStock) {
                log.info("Order-Service placeOrder -- if Block");
                var order = mapToOrder(orderRequest);
                orderRepository.save(order);
                log.info("Order-Service placeOrder -- if Block order Saved: {}", order);
            }else {
                log.info("Order-Service placeOrder -- else Block");
                throw new ProductNotFoundException("Product with SkuCode : " + orderRequest.skuCode() + " is not in Stock");
            }
        }).exceptionally(ex ->{
            // exceptionally is used to handle any exceptions that occur
            log.error("Error placing order", ex);
            throw new RuntimeException("Error placing order", ex);
        }).join();  // This will block until the CompletableFuture completes. join() is used to block and wait for the CompletableFuture to complete.
    }*/

    // when InventoryClient abstract method return type is Boolean then use below method
    // placeOrder() and placeOrderWithObservation() difference is that I have used Observation to set TraceID to ProducerRecord by adding KafkaHeader
    public void placeOrder(OrderRequest orderRequest) {
        boolean inStock = inventoryClient.isInStock(orderRequest.skuCode(), orderRequest.quantity());
        log.info("Order-Service placeOrder -- inStock:: {}", inStock);
         if (!inStock){
             log.warn("Order-Service placeOrder -- if Block");
             throw new ProductNotFoundException("Product with SkuCode : " + orderRequest.skuCode() + " is not in Stock");
         }
         var order = mapToOrder(orderRequest);
         orderRepository.save(order);
         log.info("Order-Service placeOrder -- order Saved: {}", order);

         // send the message to Kafka Topic
        // orderNumber and email for now
        OrderPlacedEvent orderPlacedEvent= new OrderPlacedEvent(
                order.getOrderNumber(),
                orderRequest.userDetails().email(),
                orderRequest.userDetails().firstName(),
                orderRequest.userDetails().lastName(),
                orderRequest.skuCode(),
                orderRequest.price(),
                orderRequest.quantity()
        );

        log.info("Start- Sending OrderPlacedEvent {} to Kafka Topic", orderPlacedEvent);
        kafkaTemplate.send(topicName, orderPlacedEvent);
        log.info("End- Sending OrderPlacedEvent {} to Kafka Topic", orderPlacedEvent);
    }

    public void placeOrderWithObservation(OrderRequest orderRequest) {
        boolean inStock = inventoryClient.isInStock(orderRequest.skuCode(), orderRequest.quantity());
        log.info("Order-Service placeOrder -- inStock:: {}", inStock);
        if (!inStock){
            log.warn("Order-Service placeOrder -- if Block");
            throw new ProductNotFoundException("Product with SkuCode : " + orderRequest.skuCode() + " is not in Stock");
        }
        var order = mapToOrder(orderRequest);
        orderRepository.save(order);
        log.info("Order-Service placeOrder -- order Saved: {}", order);

        // send the message to Kafka Topic
        // orderNumber and email for now
        // OrderPlacedEvent created
        OrderPlacedEvent orderPlacedEvent= new OrderPlacedEvent(
                order.getOrderNumber(),
                orderRequest.userDetails().email(),
                orderRequest.userDetails().firstName(),
                orderRequest.userDetails().lastName(),
                orderRequest.skuCode(),
                orderRequest.price(),
                orderRequest.quantity()
        );

        log.info("Start- Sending OrderPlacedEvent {} to Kafka Topic", orderPlacedEvent);

        // Create and send the message with trace ID
        Observation.createNotStarted(topicName, observationRegistry).observe(() -> {
            Span currentSpan = tracer.currentSpan();
            String traceId = currentSpan != null ? currentSpan.context().traceId() : "trace-id-not-available";
            ProducerRecord<String, OrderPlacedEvent> producerRecord = new ProducerRecord<>(topicName, orderPlacedEvent);

            // both way working sending KafkaHeaders.CORRELATION_ID or traceId equal traceId at order and notification service
            producerRecord.headers().add(KafkaHeaders.CORRELATION_ID, traceId.getBytes());
            //producerRecord.headers().add("traceId", traceId.getBytes());

            log.info("placeOrder with KafkaHeaders using ObservationRegistry :: Span: {}", currentSpan);
            log.info("placeOrder with KafkaHeaders using ObservationRegistry :: tracer: {}", tracer);
            log.info("placeOrder with KafkaHeaders using ObservationRegistry :: traceId: {}", traceId);



            // // Send the event asynchronously
            CompletableFuture<SendResult<String, OrderPlacedEvent>> sendResultOrder = kafkaTemplate.send(producerRecord);


            // To Send Kafka Message Synchronously   ::: simply append .get() to the send() method This will block your application’s flow until the send operation is complete
            /*
                send() is asynchronous and returns a ListenableFuture object, which you can use to register a callback to execute code once the future is complete.
                However, when you call .get() on this ListenableFuture, you’re telling your code to “wait here” until the result of the future is available.

                The .get() method is a blocking call. This means that your application will stop at this line and will not move to the next line until
                Kafka has responded with the result of the send operation. This could be a confirmation that the message was successfully sent, or
                an exception if something went wrong.

                In Kafka, when a message is published to a topic, it’s not just thrown into a digital void.
                It’s methodically stored in a partition within a topic at a specific position known as an offset.
                The SendResult object’s getRecordMetadata() method provides this exact information.

                The partition is like a sub-container within a topic. Topics in Kafka are split into partitions to allow for scaling
                (more partitions mean more potential for parallel processing) and fault tolerance. When you log result.getRecordMetadata().partition(),
                 you’re getting the partition number where your message was stored. It’s useful for understanding how Kafka is distributing your messages across the topic.

                 This line confirms the topic to which the message was sent.
                 It’s a straightforward piece of data but serves as a good check to ensure your message is going to the right place.

                 The offset is a unique identifier for each message within a partition. You can think of it as the address of your message in Kafka’s storage.
                 Logging the offset helps you track exactly where the message lands in the partition.
                 It can be particularly useful for debugging purposes or for setting up systems that might need to read from a specific point in the topic.


             */

            // To Send Kafka Message Synchronously
            /*try {
                SendResult<String, OrderPlacedEvent> result =
                        kafkaTemplate.send(topicName,orderPlacedEvent).get();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            }*/

            // // Handle the sendResultOrder completable future
            // add a callback to the CompletableFuture that will be called when the message is successfully sent or if there is an error:
            sendResultOrder.whenComplete((result,exception) -> {
                if (exception != null) log.error("Failed to send message: " + exception.getMessage());
                else {
                    log.info("Message sent successfully: to Notification-Service" + result.getRecordMetadata().toString());
                    log.info("RecordMetaData Partition : {}" , result.getRecordMetadata().partition());
                    log.info("RecordMetaData Topic : {}" , result.getRecordMetadata().topic());
                    log.info("RecordMetaData Offset : {}" , result.getRecordMetadata().offset());
                }
            });
            // Optionally, wait for the result synchronously
            // future.join();

        });

        log.info("End- Sending OrderPlacedEvent {} to Kafka Topic", orderPlacedEvent);
    }


    private static Order mapToOrder(OrderRequest orderRequest) {
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());
        order.setPrice(orderRequest.price());
        order.setQuantity(orderRequest.quantity());
        order.setSkuCode(orderRequest.skuCode());
        return order;
    }
}
