package com.mylearning.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(EmailException.class)
    public ResponseEntity<?> handleMailException(EmailException ex) {
        log.info("GlobalExceptionHandler.handleMailException Called: {} ", ex.getMessage());
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.SERVICE_UNAVAILABLE);
    }

    @ExceptionHandler(PdfException.class)
    public ResponseEntity<?> handlePdfException(PdfException ex) {
        log.info("GlobalExceptionHandler.handlePdfException Called: {} ",ex.getMessage());
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NO_CONTENT);
    }

    @ExceptionHandler(MyMessagingException.class)
    public ResponseEntity<?> handleMyMessagingException(MyMessagingException ex) {
        log.info("GlobalExceptionHandler.handlePdfException Called: {} ",ex.getMessage());
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.EXPECTATION_FAILED);
    }
}
