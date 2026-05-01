package com.microfinanceBank.Loan.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 대출 위험도 분석 결과 엔티티.
 * 차용자의 신용도·월 상환액·잔여 가용 금액·신용 점수를 저장한다.
 */
@Entity
@Data
@NoArgsConstructor
public class LoanRiskAnalysis implements Serializable {

    /** 분석 ID (기본키). */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    /** 대출 적합 여부 (true: 적합, false: 부적합). */
    private boolean loanWorthinessAnalysis;
    /** 예상 월 상환액. */
    private BigDecimal monthlyPayment;
    /** 지출 후 남는 금액 (상환 가능성 판단 기준). */
    private BigDecimal amountLeftAfterExpenses;
    /** 신용 점수 등급. */
    private int creditScoreRating;

}
