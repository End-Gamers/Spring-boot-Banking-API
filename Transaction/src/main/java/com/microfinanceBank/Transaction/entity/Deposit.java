package com.microfinanceBank.Transaction.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;

/**
 * 입금(Deposit) 거래 엔티티.
 * Transaction을 상속하며 TRANSACTION_TYPE = 'DEPOSIT' 으로 구분된다.
 */
@Data
@AllArgsConstructor
//@Table(indexes={@Index(columnList="uid,name",name="Index_user")})
@NoArgsConstructor
@DiscriminatorValue(value = "DEPOSIT")
@Entity
public class Deposit extends  Transaction {

    /** 입금 금액 (양수여야 함). */
    @Column(nullable = false,updatable = false)
    @Positive
    private BigDecimal amount;


}
