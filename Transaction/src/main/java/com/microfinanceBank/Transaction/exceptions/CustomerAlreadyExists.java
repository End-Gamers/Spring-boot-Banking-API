package com.microfinanceBank.Transaction.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/** 고객이 이미 존재할 때 발생하는 예외 (HTTP 409 Conflict). */
@ResponseStatus(HttpStatus.CONFLICT)
public class CustomerAlreadyExists extends RuntimeException {

    public CustomerAlreadyExists(String message) {
        super(message);
    }

}
