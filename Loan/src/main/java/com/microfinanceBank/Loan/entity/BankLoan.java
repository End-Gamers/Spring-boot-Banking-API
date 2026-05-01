package com.microfinanceBank.Loan.entity;


import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 은행 대출(BankLoan) 엔티티.
 * Loan을 상속하며 LOAN_TYPE = 'bank-loan' 으로 구분된다.
 * 은행이 제시하는 대출 조건(LoanOffer)과 연결된다.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@DiscriminatorValue(value = "bank-loan")
public class BankLoan extends Loan implements Serializable {

    /** 적용된 은행 대출 조건(이자율, 연체 이자율 등). */
    @OneToOne
    @JoinColumn(name = "bank_loan_offer_id")
    protected LoanOffer loanOffer;


}
