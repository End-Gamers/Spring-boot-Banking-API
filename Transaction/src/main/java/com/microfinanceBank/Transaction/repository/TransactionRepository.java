package com.microfinanceBank.Transaction.repository;

import com.microfinanceBank.Transaction.dto.TransactionEntityDto;
import com.microfinanceBank.Transaction.entity.Deposit;
import com.microfinanceBank.Transaction.entity.Transaction;
import com.microfinanceBank.Transaction.enums.TransactionType;
import com.microfinanceBank.Transaction.projections.ITransaction;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

/**
 * 거래(Transaction) 데이터 접근 레포지토리.
 * 성공/실패 입금 조회 및 계좌별 전체 거래 내역 페이지네이션 쿼리를 제공한다.
 */
//@NoRepositoryBean
public interface TransactionRepository extends JpaRepository<Transaction,Long> {

    /** 특정 유형에서 성공 처리된 거래 목록을 조회한다. */
    @Query("select t from Transaction t " +
            "join fetch t.transactionDetail td " +
            "where t.transactionType= :type and " +
            "td.transactionStatus= 'SUCCESS'")
    List<Transaction> findAllSuccessfulDepositsTransactions(@Param("type") TransactionType type);

    @Query("select t from Transaction t " +
            "join fetch t.transactionDetail td " +
            "where t.transactionType ='DEPOSIT' and " +
            "td.transactionStatus <> 'SUCCESS' ")
    List<Deposit> findAllFailedDepositTransactions();

    @Query("select t from Transaction t " +
            "join fetch  t.transactionDetail td " +
            "where (t.recipientAccount = :account_num " +
            "or t.sourceAccount =   :account_num ) " +
            "order by t.transactionDate , t.time" )
//      @Query(value = "select * from transaction t" +
//              " where t.source_account = :account_num " +
//              " or t.desc_acct= :account_num " +
//              "order by transaction_date, time" ,nativeQuery = true)
      List<ITransaction> findAllCustomersTransactions(@Param("account_num") Long account_num, Pageable pageable);


}
