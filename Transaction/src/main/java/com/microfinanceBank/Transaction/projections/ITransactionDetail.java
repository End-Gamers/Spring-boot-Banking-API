package com.microfinanceBank.Transaction.projections;

import com.microfinanceBank.Transaction.enums.TransactionStatus;

import java.sql.Date;
import java.sql.Time;

/**
 * 거래 세부 정보 프로젝션 인터페이스.
 * 거래 ID·상태·날짜·시각을 조회하는 뷰를 정의한다.
 */
public interface ITransactionDetail {
    /** 거래 상세 ID를 반환한다. */
    Long getTransactionId();
    /** 거래 처리 상태를 반환한다. */
    TransactionStatus getTransactionStatus();
    /** 거래 날짜를 반환한다. */
     Date getTransactionDate();
    /** 거래 시각을 반환한다. */
     Time getTime();


}
