package com.mylearning.orderservice.service;

/*import com.mylearning.orderservice.dto.InventoryResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import com.mylearning.orderservice.event.OrderPlacedEvent;
import org.springframework.beans.factory.annotation.Autowired;
import brave.Span;
import brave.Tracer;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.Arrays;*/
import com.mylearning.orderservice.model.Order;
import com.mylearning.orderservice.model.OrderLineItems;
import com.mylearning.orderservice.repository.OrderRepository;
import com.mylearning.orderservice.dto.OrderLineItemsDto;
import com.mylearning.orderservice.dto.OrderRequest;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;



import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;

    //private String inventoryUrl="http:'//localhost:8088/api/inventory";

    private String inventoryUrl="http://INVENTORY-SERVICE/api/inventory";

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    // when we are making a call in a single thread without any creating additional threads we can trace
    // the request from start to end but if we are trying to create an additional thread on top of the additional thread
    // then this is not really useful we need to create our own spans.... just like here in this example api-gateway calling order-service
    // and order-service calling inventory-service this is all in one thread but  when inventory-service is down then inside order-service we used circuit-breaker and for worst case
    // in order-service retry mechanism and time-out mechanism to call inventory-service but inventory-service has delay due to thread.sleep we implemented
    // so retry of order-service will retry call to inventory-service and this will be treated as additional thread and so when
    // order-service is calling inventory-service zipkin is not tracing the call generated from order-service to inventory-service only inventory-service is been traced and registered in zipkin
    // so we need to create our own span to trace and register it in zipkin
    // spring-cloud-sleuth provides a mechanism to actually create our own span-ids .. for that we can use Trace class from spring-cloud-sleuth
    //private final Tracer tracer;

    //@Autowired
    //private final WebClient.Builder webClientBuilder;

    //private final KafkaTemplate<String, OrderPlacedEvent> kafkaTemplate;

    @Transactional
    public String placeOrder(OrderRequest orderRequest) {
        /*
        placeOrder(orderRequest) :: accepting orderrequest dto which contains list of OrderLineItemsDto
        to extract OrderLineItemsDto and then set OrderLineItems using builder pattern and setting it to order
        to get the list of skucodes of each OrderLineItems to use skucodes to check and verify and validate availabality of product
        with the Inventory-Service before placing the order using InventoryResponse as dto here in Order-Service
         */
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());

        List<OrderLineItems> orderLineItems = orderRequest.orderLineItemsDtoList()
                .stream()
                .map(this::mapToDto)
                        .collect(Collectors.toList());

        order.setOrderLineItemsList(orderLineItems);

        // CALLING INVENTORY-SERVICE AND PLACING ORDER IF PRODUCT IS-IN-STOCK

        // collecting all the skucodes from the OrderLineItems of order in order to use it to verify the availabality of product by getting the response from Inventory-Service
        /*List<String> skuCodes=order.getOrderLineItemsList()
                .stream()
                .map(OrderLineItems::getSkuCode)
                .collect(Collectors.toList());*/

        // just before making a call to inventory-service creating Custom Span we are giving unique name for span i.e. InventoryServiceLookup
        //Span inventoryServiceLookup = tracer.nextSpan().name("InventoryServiceLookup");

        // spring cloud sleuth will assign this particular span-id which we have created in the above step to this particular piece of code which is executed inside below block of code
        //try (Tracer.SpanInScope isLookup = tracer.withSpanInScope(inventoryServiceLookup.start())) {

            //inventoryServiceLookup.tag("call", "inventory-service");

            // getting response from Inventory-Service to collect isInStock details for each OrderLineItems of an order
            //InventoryResponse[] responses = webClientBuilder.build().get()
              //      .uri(inventoryUrl,
                //            uriBuilder -> uriBuilder.queryParam("skuCode", skuCodes).build())
                  //  .retrieve() // to be able to retrieve the response we use retrieve()
                    //.bodyToMono(InventoryResponse[].class) // defining what is the type of response we are returning from Inventory-Service
                    //.block();  // by default webclient makes asynchronous request so to be able to make synchronous request with webclient we use block()

            // when all the products of the order we place isInStock is true in InventoryResponse then only below line of code will return true
            //boolean allProductsInStock = Arrays.stream(responses).allMatch(InventoryResponse::isInStock);

            //if (allProductsInStock) {

                orderRepository.save(order);

                // we can also define the notification topic as the default topic for order service so if multiple messages are sending to kafka so instead of typing this
              // notificationTopic for every message we can define this notificationTopic as default topic so that springboot will understand we have to always send the messages unless we explicitly provide
            // a different topic name so springboot will understand that it should always send the messages to this notificationTopic
                //kafkaTemplate.send("notificationTopic", new OrderPlacedEvent(order.getOrderNumber()));

                return "Order Placed Successfully";
            /*} else {
                throw new IllegalArgumentException("Product is not in stock, please try again later");
            }
        }finally {
            inventoryServiceLookup.flush();
        }*/
    }

    // mapping the OrderLineItemsDto to OrderLineItems
    private OrderLineItems mapToDto(OrderLineItemsDto orderLineItemsDto) {
        return new OrderLineItems(orderLineItemsDto.id(), orderLineItemsDto.skuCode(),orderLineItemsDto.price(),orderLineItemsDto.quantity());
    }
}
