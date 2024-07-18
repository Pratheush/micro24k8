package com.mylearning.config;

import feign.Response;
import feign.codec.DecodeException;
import feign.codec.Decoder;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.concurrent.CompletableFuture;

//@Configuration
public class FeignConfig {
    @Bean
    public Decoder feignDecoder() {
        return new CompletableFutureDecoder();
    }

    public static class CompletableFutureDecoder implements Decoder {
        private final ObjectMapper objectMapper = new ObjectMapper();

        @Override
        public Object decode(Response response, Type type) throws IOException {
            if (type instanceof Class<?> && CompletableFuture.class.isAssignableFrom((Class<?>) type)) {
                Type actualType = ((Class<?>) type).getTypeParameters()[0];
                if (actualType == Boolean.class) {
                    Boolean result = objectMapper.readValue(response.body().asInputStream(), Boolean.class);
                    return CompletableFuture.completedFuture(result);
                }
            }
            throw new UnsupportedOperationException("Unsupported type: " + type);
        }
    }
}