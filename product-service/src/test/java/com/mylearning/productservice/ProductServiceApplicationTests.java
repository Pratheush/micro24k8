package com.mylearning.productservice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mylearning.productservice.dto.ProductRequest;
import com.mylearning.productservice.dto.ProductResponse;
import com.mylearning.productservice.model.Product;
import com.mylearning.productservice.repository.ProductRepository;
import com.mylearning.productservice.service.ProductService;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.junit.jupiter.api.Assertions;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static net.bytebuddy.matcher.ElementMatchers.is;
import static org.hamcrest.Matchers.hasSize;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@Testcontainers     // using this junit understands that we are going to use testcontainers to run this particular test
@AutoConfigureMockMvc // use this annotation to autoconfigure MockMvc
class ProductServiceApplicationTests {

    // defining the mongodb container inside our tests
    @Container // this annotation tells junit that this is a mongodb  container
    static final MongoDBContainer mongoDBContainer = new MongoDBContainer(DockerImageName.parse("mongo:4.4.10-focal"));

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ProductRepository productRepository;

    @Mock
    private ProductService productService;

    // setting the uri property of mongodb container dynamically in our test properties
    @DynamicPropertySource // this will add this property to our spring context dynamically at the time of running test
    static void setProperties(DynamicPropertyRegistry dymDynamicPropertyRegistry) {
        dymDynamicPropertyRegistry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    @Test
    void shouldCreateProduct() throws Exception {
        ProductRequest productRequest = getProductRequest();
        String productRequestString = objectMapper.writeValueAsString(productRequest);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/product")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(productRequestString))  // here we have to provide content as String
                .andExpect(status().isCreated()); // this is status() is static coming from mockMvc result matchers
        Assertions.assertEquals(1, productRepository.findAll().size());
    }

    private ProductRequest getProductRequest() {
        return ProductRequest.builder()
                .name("iPhone 13")
                .description("iPhone 13")
                .price(BigDecimal.valueOf(1200))
                .build();
    }

@Test
    void shouldGetAllProducts() throws Exception {

    List<ProductResponse> expected = new ArrayList<>();
    expected.add(getProductResponse());

    Mockito.when(productService.getAllProducts()).thenReturn(expected);

    mockMvc.perform(MockMvcRequestBuilders.get("/api/product"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpectAll(MockMvcResultMatchers.jsonPath("$", hasSize(1)));

    Assertions.assertEquals(1, productRepository.findAll().size());
    }
    private ProductResponse getProductResponse() {
        return ProductResponse.builder()
                .id("1")
               .name("iPhone 13")
                .description("iPhone 13")
                .price(BigDecimal.valueOf(1800))
                .build();
    }
}
