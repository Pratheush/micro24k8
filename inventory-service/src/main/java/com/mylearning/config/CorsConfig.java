package com.mylearning.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/*
   defined CORS configuration as we will be accessing different services through the browser from API Gateway.
    update the microservices to define CORS, or else we will get a CORS ERROR while accessing the API Documentation.
 */
@Configuration
@Slf4j
class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        log.info("Inventory-Service CorsConfig addCorsMappings corsRegistry: {}" , registry);
        registry.addMapping("/api/**")
                .allowedMethods("*")
                .allowedHeaders("*")
                .allowedOriginPatterns("*")
                .allowCredentials(false);
    }
}