package com.example.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class UpdateCustomerInvalidatedException extends RuntimeException {
    public UpdateCustomerInvalidatedException(String message) {
        super(message);
    }
}
