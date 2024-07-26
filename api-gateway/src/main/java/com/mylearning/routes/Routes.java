package com.mylearning.routes;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.server.mvc.filter.CircuitBreakerFilterFunctions;
import org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions;
import org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.function.RequestPredicates;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

import java.net.URI;


import static org.springframework.cloud.gateway.server.mvc.filter.FilterFunctions.setPath;
import static org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions.route;
import static org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions.http;

/*
       When you set proxyBeanMethods = false, no proxy methods are created.
       Each call to a method will create a new instance of the bean, acting just like a factory method.

       In contrast, when proxyBeanMethods is true, Spring intercepts method calls and ensures that the
       same bean instance is returned for subsequent calls. This behavior is useful for enforcing
       bean lifecycle behavior, such as returning shared singleton bean instances even when called directly in user code
 */
@Configuration(proxyBeanMethods = false)
@Slf4j
public class Routes {

    //
    // route() method takes in two arguments one for the path which is the predicate we want to match in this case
    // (/api/product), and the second argument is http(“<target-destination-url>”)
    // which points to the target destination i.e. product service that is running at http://localhost:8086
    // use Filters to implement Circuit Breakers for resiliency.

    @Value("${forward.fallbackRoute}")
    private String forwardPath;

    @Value("${apidocs.path}")
    private String apiDocsPath;

    @Bean
    public RouterFunction<ServerResponse> productServiceRoute() {
        log.info("Routes >>> productServiceRoute");
        return GatewayRouterFunctions.route("product_service")
                .route(RequestPredicates.path("/api/product"), HandlerFunctions.http("http://localhost:8086"))
                .filter(CircuitBreakerFilterFunctions.circuitBreaker("productServiceCircuitBreaker", URI.create(forwardPath)))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> orderServiceRoute() {
        log.info("Routes >>> orderServiceRoute");
        return route("order_service")
                .route(RequestPredicates.path("/api/order"), http("http://localhost:8087"))
                .filter(CircuitBreakerFilterFunctions.circuitBreaker("orderServiceCircuitBreaker", URI.create(forwardPath)))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> inventoryServiceRoute() {
        log.info("Routes >>> inventoryServiceRoute");
        return route("inventory_service")
                .route(RequestPredicates.path("/api/inventory"), http("http://localhost:8088"))
                .filter(CircuitBreakerFilterFunctions.circuitBreaker("inventoryServiceCircuitBreaker", URI.create(forwardPath)))
                .build();
    }

    // We defined each service with a separate URL in application.properties, whenever the user visits this URL, we have to route this request to
    // the appropriate service, and for that, we need to add the corresponding routes in the Routes.java class
    // configuration will route all the incoming requests to the /api-docs path of the corresponding service.
    /*
        Listens for requests to /aggregate/product-service/v3/api-docs.    >>>>>    Forwards those requests to http://localhost:8086/api-docs.

        Applies a circuit breaker named productServiceSwaggerCircuitBreaker to handle failures gracefully.
        If the downstream service is unavailable, the request is forwarded to a fallback route /fallbackRoute.

        RouterFunction is used to define routes in a Spring WebFlux application
        GatewayRouterFunctions.route("product_service_swagger")   This initializes the route with an identifier "product_service_swagger"

        .route(RequestPredicates.path("/aggregate/product-service/v3/api-docs"), HandlerFunctions.http("http://localhost:8086"))
        This means that when a request matches the path /aggregate/product-service/v3/api-docs, it will be forwarded to the Product Service running on localhost at port 8086

        RequestPredicates.path("/aggregate/product-service/v3/api-docs")
        This predicate matches requests that have the specified path. Essentially, it listens for requests to /aggregate/product-service/v3/api-docs.
        HandlerFunctions.http("http://localhost:8086"): This handler forwards the request to http://localhost:8086

        .filter(CircuitBreakerFilterFunctions.circuitBreaker("productServiceSwaggerCircuitBreaker", URI.create("forward:/fallbackRoute")))
        Adds a circuit breaker filter to the route
        This applies a circuit breaker named "productServiceSwaggerCircuitBreaker".
        URI.create("forward:/fallbackRoute"): Specifies a fallback route to be used if the circuit breaker is triggered.
         If the circuit breaker is open (indicating that the downstream service is failing), the request will be forwarded to the fallback route /fallbackRoute

         .filter(setPath("/api-docs"))
          Changes the request path before forwarding it.
          The setPath filter modifies the request path to /api-docs. This means that when the request is forwarded to http://localhost:8086, it will use the path /api-docs

     */
    // http://localhost:8086/aggregate/product-service/v3/api-docs >>> http://localhost:8086/api-docs
    @Bean
    public RouterFunction<ServerResponse> productServiceSwaggerRoute() {
        log.info("Routes >>> productServiceSwaggerRoute");
        return GatewayRouterFunctions.route("product_service_swagger")
                .route(RequestPredicates.path("/aggregate/product-service/v3/api-docs"), HandlerFunctions.http("http://localhost:8086"))
                .filter(CircuitBreakerFilterFunctions.circuitBreaker("productServiceSwaggerCircuitBreaker", URI.create(forwardPath)))
                .filter(setPath(apiDocsPath))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> orderServiceSwaggerRoute() {
        log.info("Routes >>> orderServiceSwaggerRoute");
        return GatewayRouterFunctions.route("order_service_swagger")
                .route(RequestPredicates.path("/aggregate/order-service/v3/api-docs"), HandlerFunctions.http("http://localhost:8087"))
                .filter(CircuitBreakerFilterFunctions.circuitBreaker("orderServiceSwaggerCircuitBreaker", URI.create(forwardPath)))
                .filter(setPath(apiDocsPath))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> inventoryServiceSwaggerRoute() {
        log.info("Routes >>> inventoryServiceSwaggerRoute");
        return GatewayRouterFunctions.route("inventory_service_swagger")
                .route(RequestPredicates.path("/aggregate/inventory-service/v3/api-docs"), HandlerFunctions.http("http://localhost:8088"))
                .filter(CircuitBreakerFilterFunctions.circuitBreaker("inventoryServiceSwaggerCircuitBreaker", URI.create(forwardPath)))
                .filter(setPath(apiDocsPath))
                .build();
    }

    // adding the fallback route and sending the response :: "Service Unavailable, please try again later"
    @Bean
    public RouterFunction<ServerResponse> fallbackRoute() {
        log.info("Routes >>> fallbackRoute");
        return route("fallbackRoute")
                .GET("/fallbackRoute", request -> ServerResponse.status(HttpStatus.SERVICE_UNAVAILABLE)
                        .body("Service Unavailable, please try again later"))
                .build();
    }
}
