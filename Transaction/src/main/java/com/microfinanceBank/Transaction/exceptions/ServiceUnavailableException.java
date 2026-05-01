package com.microfinanceBank.Transaction.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/** 다운스트림 서비스가 응답하지 않을 때 발생하는 예외 (HTTP 503 Service Unavailable). */
@ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
public class ServiceUnavailableException extends RuntimeException {
    public ServiceUnavailableException(String message) {
        super(message);
    }


}
