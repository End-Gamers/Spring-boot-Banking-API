package com.microfinanceBank.Transaction.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/** 잔액 부족 시 발생하는 예외 (HTTP 400 Bad Request). */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InsufficientException extends RuntimeException {
    public InsufficientException(String message) {
        super(message);
    }


}
