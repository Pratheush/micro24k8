package com.mylearning.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NO_CONTENT)
@Slf4j
public class PdfException extends RuntimeException {
    public PdfException(String message) {
        super(message);
        log.info("PdfException error:: {}", message);
    }
}
