package com.microfinanceBank.Loan.service;

import com.microfinanceBank.Loan.dto.LoanRequest;

import java.math.BigDecimal;

/**
 * 대출 금액 계산 서비스 인터페이스.
 * 월 상환액·총 상환액·총 이자·연체 이자 계산 기능을 정의한다.
 */
public interface LoanCalculation {
    /**
     * 월 상환액을 계산한다.
     * @param principal       원금
     * @param interest        연 이자율 (%)
     * @param numberOfPayments 납입 횟수
     * @return 월 상환액
     */
    BigDecimal calculateMonthlyPayments(BigDecimal principal, double interest, int numberOfPayments);

    /**
     * 총 상환액(원금 + 이자)을 계산한다.
     * @param principal       원금
     * @param interest        연 이자율 (%)
     * @param numberOfPayments 납입 횟수
     * @return 총 상환액
     */
    BigDecimal calculateTotalAmountToBePaid(BigDecimal principal, double interest, int numberOfPayments);

    /**
     * 납부해야 할 총 이자액을 계산한다.
     * @param principal       원금
     * @param interest        연 이자율 (%)
     * @param numberOfPayments 납입 횟수
     * @return 총 이자액
     */
    BigDecimal calculateTotalInterestToBePaid(BigDecimal principal, double interest, int numberOfPayments);

    /**
     * 연체 이자를 계산한다.
     * @param amount              연체 원금
     * @param numberOfDaysLate    연체 일수
     * @param latePaymentInterest 연체 이자율 (%)
     * @return 연체 이자액
     */
    BigDecimal calculateLatePayments(BigDecimal amount, int numberOfDaysLate, double latePaymentInterest );


}
