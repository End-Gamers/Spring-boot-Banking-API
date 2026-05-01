package com.microfinanceBank.Transaction.exceptions.handler;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.time.ZonedDateTime;

/**
 * API 예외 응답 메시지 모델.
 * 오류 메시지, HTTP 상태 코드, 발생 시각을 포함한다.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiExceptionMessage {
    /** 오류 설명 메시지. */
    public  String message;
    /** HTTP 응답 상태 코드. */
    public HttpStatus status;
    /** 오류 발생 시각 (UTC+1 기준). */
    public ZonedDateTime timeStamp;


}
