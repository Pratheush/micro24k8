package com.mylearning.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
@Slf4j
public class EmailException extends RuntimeException {
    public EmailException(String message) {
        super(message);
    }
}
