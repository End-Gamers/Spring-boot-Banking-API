package com.microfinanceBank.Transaction.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/** 고객 또는 계좌를 찾을 수 없을 때 발생하는 예외 (HTTP 404 Not Found). */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class NoCustomerExceptions extends RuntimeException {

    public NoCustomerExceptions(String message) {
        super(message);
    }

}
