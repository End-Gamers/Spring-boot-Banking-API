package com.microfinanceBank.Transaction.projections;

import java.math.BigDecimal;

/**
 * 출금 거래 프로젝션 인터페이스.
 * ITransaction을 확장하여 출금 금액을 추가로 조회한다.
 */
public interface IWithdraw extends ITransaction {
     /** 출금 금액을 반환한다. */
     BigDecimal getAmount();

}
