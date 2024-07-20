package com.mylearning.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.EXPECTATION_FAILED)
@Slf4j
public class MyMessagingException extends Exception{
    public MyMessagingException(String message){
        super(message);
      log.info("MyMessagingException :: message :: {} ",message);
    }
}
