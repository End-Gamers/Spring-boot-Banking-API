package com.microfinanceBank.Transaction.dto;

import com.microfinanceBank.Transaction.entity.TransactionDetail;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;

/**
 * 입금 거래 응답 DTO.
 * 입금 ID·금액·출금 계좌·거래 상세·날짜·시각 정보를 담는다.
 */
@Data
@NoArgsConstructor
public class DepositDto {
    /** 거래 ID. */
    private Long id;
    /** 입금 금액. */
    private BigDecimal amount;
    /** 출금(원천) 계좌 번호. */
    private Long sourceAccount;
    /** 거래 세부 정보. */
    private TransactionDetail transactionDetail;
    /** 거래 날짜. */
    private Date transactionDate;
    /** 거래 시각. */
    private Time time;
}
