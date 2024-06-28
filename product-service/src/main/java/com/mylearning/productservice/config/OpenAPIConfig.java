package com.mylearning.productservice.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenAPIConfig {
    @Value("${product.openapi.dev-url}")
    private String devUrl;

    @Bean
    public OpenAPI myOpenAPI() {
        Server devServer = new Server();
        devServer.setUrl(devUrl);
        devServer.setDescription("Server URL in Development environment");

        Contact contact = new Contact();
        contact.setEmail("raj@gmail.com");
        contact.setName("Raj");
        contact.setUrl("https://www.rajkodin.com");

        License mitLicense = new License().name("MIT License").url("https://choosealicense.com/licenses/mit/");

        Info info = new Info()
                .title("Product Service API")
                .version("1.0")
                .contact(contact)
                .description("This is the REST API for Product Service")
                .termsOfService("https://www.termsandconditionsgenerator.com/")
                .license(mitLicense);

        return new OpenAPI()
                .info(info)
                .externalDocs(new ExternalDocumentation()
                        .description("You can refer to the Product Service Wiki Documentation")
                        .url("https://product-service-dummy-url.com/docs"))
                .servers(List.of(devServer));


    }

}
