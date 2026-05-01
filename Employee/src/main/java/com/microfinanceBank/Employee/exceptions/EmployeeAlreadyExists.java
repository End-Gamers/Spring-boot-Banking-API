package com.microfinanceBank.Employee.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/** 직원이 이미 존재할 때 발생하는 예외 (HTTP 409 Conflict). */
@ResponseStatus(HttpStatus.CONFLICT)
public class EmployeeAlreadyExists extends RuntimeException {

    public EmployeeAlreadyExists(String message) {
        super(message);
    }

}
