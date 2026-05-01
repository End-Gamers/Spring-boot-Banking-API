package com.microfinanceBank.Transaction.repository;

import com.microfinanceBank.Transaction.entity.Deposit;
import com.microfinanceBank.Transaction.enums.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


/** 입금(Deposit) 전용 레포지토리. TransactionRepository를 상속하여 추가 쿼리를 정의할 수 있다. */
public interface DepositRepository extends TransactionRepository {


}
