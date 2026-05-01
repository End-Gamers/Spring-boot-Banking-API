package com.microfinanceBank.Transaction.projections;

import java.math.BigDecimal;

/**
 * 송금 거래 프로젝션 인터페이스.
 * ITransaction을 확장하여 송금 금액과 수신 계좌 번호를 추가로 조회한다.
 */
public interface ITransfer extends ITransaction{
     /** 송금 금액을 반환한다. */
     BigDecimal getAmount();
     /** 수신 계좌 번호를 반환한다. */
     Long getRecipientAccount();


}
