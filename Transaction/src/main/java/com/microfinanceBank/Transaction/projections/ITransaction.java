package com.microfinanceBank.Transaction.projections;

import com.microfinanceBank.Transaction.enums.TransactionType;

import java.sql.Date;
import java.sql.Time;

/**
 * 거래 엔티티 프로젝션 인터페이스.
 * 거래 ID·출금 계좌·상세 정보·날짜·유형을 조회하는 기본 뷰를 정의한다.
 */
public interface ITransaction {
     /** 거래 ID를 반환한다. */
     Long getId();
     /** 출금 계좌 번호를 반환한다. */
     Long getSourceAccount();
     /** 거래 세부 정보를 반환한다. */
     ITransactionDetail getTransactionDetail();
     /** 거래 날짜를 반환한다. */
     Date getTransactionDate();
     /** 거래 시각을 반환한다. */
     Time getTime();
     /** 거래 종류(입금/출금/송금)를 반환한다. */
     TransactionType getTransactionType();
}
