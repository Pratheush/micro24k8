package com.mylearning.config;

import com.mylearning.aspect.PerformanceTrackerHandler;
import com.mylearning.model.Product;
import com.mylearning.repository.ProductRepository;
import com.mylearning.service.ProductService;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import io.micrometer.observation.aop.ObservedAspect;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
//@RequiredArgsConstructor
@Slf4j
public class ObservationConfig {

    //private final ProductRepository productRepository;
    @Bean
    ObservedAspect observedAspect(ProductRepository productRepository,ObservationRegistry registry) {
        /*registry.observationConfig().observationHandler(new PerformanceTrackerHandler()); // Registering our HandlerClass to track the method execution
        Observation.createNotStarted("post.load-all-products",registry)
                .lowCardinalityKeyValue("author","Raj R")
                .contextualName("post.find-all")
                .observe(()->{
                    List<Product> products = productRepository.findAll();
                    log.info("Products :: {}",products);
                });*/
        return new ObservedAspect(registry);
    }
}