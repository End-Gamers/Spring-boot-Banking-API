package com.microfinanceBank.Transaction.projections;

import com.microfinanceBank.Transaction.enums.TransactionStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.sql.Date;
import java.sql.Time;

/**
 * 거래 세부 정보 프로젝션 DTO.
 * ITransactionDetail 인터페이스를 구현하지 않지만 동일한 필드 구조를 가진다.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDetail {
    /** 거래 상세 ID. */
    private Long transactionId;
    /** 거래 처리 상태. */
    private TransactionStatus transactionStatus;
    /** 거래 날짜. */
    private Date transactionDate;
    /** 거래 시각. */
    private Time time;

}
