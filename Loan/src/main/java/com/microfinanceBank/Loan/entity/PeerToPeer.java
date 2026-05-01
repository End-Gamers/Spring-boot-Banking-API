package com.microfinanceBank.Loan.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * P2P 대출(PeerToPeer) 엔티티.
 * Loan을 상속하며 LOAN_TYPE = 'p2p' 로 구분된다.
 * 개인 대출자가 제시하는 P2P 대출 조건(P2pLoanOffer)과 연결된다.
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@DiscriminatorValue(value = "p2p")
public class PeerToPeer extends Loan implements Serializable {

    /** 적용된 P2P 대출 조건. */
    @OneToOne
    @JoinColumn(name = "p2p_loan_offer_id")
    private P2pLoanOffer loanOffer;

}
