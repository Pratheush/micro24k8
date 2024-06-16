package com.mylearning.orderservice.config;

/*import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;*/

//@Configuration
/*
public class WebClientConfig {

    */
/*@Bean
    public WebClient webClient(){
        return WebClient.builder().build();
    }*//*


    @Bean
    @LoadBalanced   // enabling client-side load balancing for distributing traffic among different instances ::
    public WebClient.Builder webClientBuilder(){     // here order-service is client and inventory-service is server to connect where we use this loadbalancing
        return WebClient.builder();
    }
}
*/
