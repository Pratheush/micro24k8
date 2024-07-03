package com.mylearning.orderservice.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

//@ResponseStatus(value = HttpStatus.NOT_FOUND)
@Slf4j
public class ProductNotFoundException extends RuntimeException{
    public ProductNotFoundException(String message){
        super(message);
        log.info("ProductNotFoundException Constructor Called");
    }
}
