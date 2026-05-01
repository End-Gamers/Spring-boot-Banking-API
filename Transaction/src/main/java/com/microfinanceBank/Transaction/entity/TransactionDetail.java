package com.microfinanceBank.Transaction.entity;

import com.microfinanceBank.Transaction.enums.TransactionStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.sql.Date;
import java.sql.Time;

/**
 * 거래 세부 정보 엔티티.
 * 거래 상태(성공/실패 등), 발생 위치, 날짜·시간을 저장한다.
 */
@Data
@NoArgsConstructor
@Entity
public class TransactionDetail {
    /** 거래 상세 ID (기본키). */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long transactionId;
    /** 거래 처리 상태 (PENDING, SUCCESS, ERROR 등). */
    @Enumerated(EnumType.STRING)
    private TransactionStatus transactionStatus;
    /** 거래 발생 위치 정보. */
    @JoinColumn
    @OneToOne(fetch = FetchType.EAGER,cascade =CascadeType.ALL)
    private Location location;
    /** 거래 날짜 (자동 생성). */
    @CreationTimestamp
    @Column(nullable = false,updatable = false)
    private Date transactionDate;
    /** 거래 시각 (자동 생성). */
    @CreationTimestamp
    @Column(nullable = false,updatable = false)
    private Time time;
}
