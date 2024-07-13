package com.mylearning.orderservice.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/*
   CORS (Cross-Origin Resource Sharing)
   defined CORS configuration as we will be accessing different services through the browser from API Gateway.
    update the microservices to define CORS, or else we will get a CORS ERROR while accessing the API Documentation.

    WebMvcConfigurer: This interface provides callback methods to customize the Java-based configuration for Spring MVC
    addCorsMappings Method: This method is overridden to add custom CORS mappings.
    1. registry.addMapping("/api/**"): This specifies that the CORS settings apply to all paths that start with /api/.
    2. .allowedMethods("*"): Allows all HTTP methods (GET, POST, PUT, DELETE, etc.) from the specified origins.
    3. .allowedHeaders("*"): Allows all headers from the specified origins.
    4. .allowedOriginPatterns("*"): Allows all origins (any domain) to make requests to the specified paths.
    5. .allowCredentials(false): Specifies that user credentials (such as cookies, authorization headers,
         or TLS client certificates) are not supported for cross-origin requests.
 */
@Configuration
@Slf4j
class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        log.info("Order-Service CorsConfig addCorsMappings corsRegistry: {}" , registry);
        registry.addMapping("/api/**")
                .allowedMethods("*")
                .allowedHeaders("*")
                .allowedOriginPatterns("*")
                .allowCredentials(false);
    }
}