package com.microfinanceBank.Transaction.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/** 계좌가 비활성 상태일 때 발생하는 예외 (HTTP 403 Forbidden). */
@ResponseStatus(HttpStatus.FORBIDDEN)
public class AccountNotActive extends RuntimeException{

    public AccountNotActive(String message) {
        super(message);
    }
}
