package com.microfinanceBank.Transaction.entity;

import ch.qos.logback.core.joran.spi.NoAutoStart;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;

/**
 * 출금(Withdraw) 거래 엔티티.
 * Transaction을 상속하며 TRANSACTION_TYPE = 'WITHDRAW' 으로 구분된다.
 */
@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@DiscriminatorValue(value = "WITHDRAW")
public class Withdraw extends Transaction{

    /** 출금 금액. */
    @Column(nullable = false,updatable = false)
    private BigDecimal amount;


}
