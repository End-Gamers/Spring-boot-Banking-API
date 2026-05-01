package com.microfinanceBank.Transaction.projections;

import java.math.BigDecimal;

/**
 * 입금 거래 프로젝션 인터페이스.
 * ITransaction을 확장하여 입금 금액을 추가로 조회한다.
 */
public interface IDeposit extends ITransaction{
     /** 입금 금액을 반환한다. */
     BigDecimal getAmount();

}
