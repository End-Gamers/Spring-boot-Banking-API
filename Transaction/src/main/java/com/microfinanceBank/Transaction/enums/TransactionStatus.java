package com.microfinanceBank.Transaction.enums;

/** 거래 처리 상태를 나타내는 열거형. */
public enum  TransactionStatus {
    /** 처리 대기 중. */
    PENDING,
    /** 취소됨. */
    CANCELLED,
    /** 성공. */
    SUCCESS,
    /** 오류 발생. */
    ERROR
}
