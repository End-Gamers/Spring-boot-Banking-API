package com.microfinanceBank.Loan.enums;

/** 대출 처리 상태를 나타내는 열거형. */
public enum LoanStatus {
    /** 초기화됨 (신청 직후). */
    INITIALIZED,
    /** 대출 적합성 분석 중. */
    WORTHINESS_ANALYSIS,
    /** 위험도 분석 중. */
    RISK_ANALYSIS,
    /** 심사 검토 중. */
    UNDER_CONSIDERATION,
    /** 승인됨. */
    ACCEPTED,
    /** 거절됨. */
    REJECTED,
    /** 취소됨. */
    CANCELLED
}
