package com.microfinanceBank.Transaction.Service;

import com.microfinanceBank.Transaction.dto.*;
import com.microfinanceBank.Transaction.projections.ITransaction;

import java.util.List;
import java.util.concurrent.CompletableFuture;


/**
 * 거래 데이터 조회 서비스 인터페이스.
 * 성공/실패 입금 조회 및 계좌별 전체 거래 내역 조회 기능을 정의한다.
 */
public interface TransactionService {
    /**
     * 성공 처리된 모든 입금 거래를 비동기로 조회한다.
     * @return DepositDto 목록 Future
     */
    CompletableFuture<List<DepositDto>> findAllSuccessfulDepositsTransactions();

    /**
     * 실패 처리된 모든 입금 거래를 비동기로 조회한다.
     * @return DepositDto 목록 Future
     */
    CompletableFuture<List<DepositDto>> findAllFailedDepositTransactions();

    /**
     * 특정 계좌의 모든 거래 내역을 페이지네이션하여 비동기로 조회한다.
     * @param accountNum 계좌 번호
     * @param offset     페이지 오프셋
     * @param size       페이지 크기
     * @return ITransaction 목록 Future
     */
    CompletableFuture<List<ITransaction>> allCustomerTransactions(Long accountNum,int offset, int size);


}
