package com.microfinanceBank.Loan.service;

import com.microfinanceBank.Loan.dto.LoanRequest;
import com.microfinanceBank.Loan.dto.LoanRequestResponse;

/**
 * 대출 서비스 인터페이스.
 * 대출 신청 처리 및 승인 기능을 정의한다.
 */
public interface LoanService {
    /**
     * 은행 대출을 신청하고 위험도 분석 메시지를 발행한다.
     * @param loanRequest 대출 신청 정보
     * @return 신청 응답 DTO
     */
    LoanRequestResponse loanRequest(LoanRequest loanRequest);

    /**
     * 대출을 승인하고 대출 개시일 및 만기일을 설정한다.
     * @param id 대출 ID
     */
    void approveLoan(String id);
}
