package com.microfinanceBank.Transaction.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;

/**
 * 송금(Transfer) 거래 엔티티.
 * Transaction을 상속하며 TRANSACTION_TYPE = 'TRANSFER' 으로 구분된다.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@DiscriminatorValue(value = "TRANSFER")
public class Transfer extends Transaction{

    /** 수신 계좌 번호 (DB 컬럼명: desc_acct). */
    @Column(nullable = false,updatable = false,name = "desc_acct")
    private Long recipientAccount;
    /** 송금 금액. */
    @Column(nullable = false,updatable = false)
    private BigDecimal amount;



}
