package com.example.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_ACCEPTABLE)
public class CreateCustomerInvalidatedException extends RuntimeException{
    public CreateCustomerInvalidatedException(String message) {
        super(message);
    }
}