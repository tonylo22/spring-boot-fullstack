package com.example.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT)
public class EmailTakenException extends RuntimeException{
    public EmailTakenException(String message) {
        super(message);
    }
}
