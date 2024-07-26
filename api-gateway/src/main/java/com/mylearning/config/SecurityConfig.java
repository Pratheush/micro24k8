package com.mylearning.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

// add the security configuration to make sure that API Gateway allows the /api-docs requests for all services without authentication.
// by adding @EnableWebSecurity to your configuration class imports the HttpSecurityConfiguration configuration class
/*
    configuring security settings for the Spring application
    1.  @Configuration: Indicates that this class contains one or more bean methods annotated
          with @Bean producing beans manageable by the Spring container.
    2.  @EnableWebSecurity: Enables Spring Security’s web security support and provides the Spring MVC integration
    3.   freeResourceUrls Variable: An array of URL patterns that are permitted without authentication.
    4.  Defines Security Filter Chain
         1.  authorizeHttpRequests: Configures authorization for different request patterns.
         2.  .requestMatchers(freeResourceUrls).permitAll(): Allows unrestricted access to URLs specified in freeResourceUrls.
         3.  .anyRequest().authenticated(): Requires authentication for any other request not specified in freeResourceUrls
    5.  CORS Configuration:
         1.  .cors(corsConfigurer -> corsConfigurer.configurationSource(corsConfigurationSource())): Applies the CORS configuration defined in the corsConfigurationSource method.
    6.  OAuth2 Resource Server:
         1.  .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults())): Configures the application to use JWT (JSON Web Token) for securing resources.

    corsConfigurationSource Method:  Defines CORS Configuration Source
    1. Creates a new CorsConfiguration object.
    2. .setAllowedOrigins(List.of("*")): Allows all origins to make requests.
    3. .setAllowedMethods(Arrays.asList("GET","POST")): Restricts allowed HTTP methods to GET and POST.
    4. .setAllowedHeaders(List.of("*")): Allows all headers in requests.
    5. UrlBasedCorsConfigurationSource : source.registerCorsConfiguration("/**", configuration): Registers the CORS configuration for all paths.

 */

@Configuration
@EnableWebSecurity
@Slf4j
public class SecurityConfig {

    // defined a variable with value where we should permit all the requests to these paths.
    // To allow the calls to the downstream microservices, we added the path /aggregate/ that covers
    // the path for all the 3 services:
    // make sure to add /actuator/prometheus in freeResourceUrls so that this path with this pattern will be permitted without authentication.
    private final String[] freeResourceUrls = {"/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**", "/swagger-resources/**", "/aggregate/**","/actuator/prometheus"};

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        log.info("SecurityConfig securityFilterChain() :: {}",httpSecurity);
        return httpSecurity
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(freeResourceUrls).permitAll()
                        .anyRequest().authenticated())
                .cors(corsConfigurer -> corsConfigurer.configurationSource(corsConfigurationSource()))
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()))
                .build();
    }

    // defined CORS configuration
    @Bean
    CorsConfigurationSource corsConfigurationSource(){
        log.info("SecurityConfig corsConfigurationSource()");
        CorsConfiguration configuration = new CorsConfiguration();
        //configuration.applyPermitDefaultValues();
        configuration.setAllowedOrigins(List.of("*"));
        configuration.setAllowedMethods(Arrays.asList("GET","POST"));
        configuration.setAllowedHeaders(List.of("*"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
