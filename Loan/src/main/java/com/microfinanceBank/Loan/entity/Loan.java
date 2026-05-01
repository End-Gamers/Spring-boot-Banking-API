package com.microfinanceBank.Loan.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.microfinanceBank.Loan.enums.LoanStatus;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 대출(Loan) 기본 엔티티 (단일 테이블 상속 전략).
 * BankLoan·PeerToPeer 자식 클래스를 LOAN_TYPE 구분자 컬럼으로 구분한다.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(
        name = "LOAN_TYPE",
        discriminatorType = DiscriminatorType.STRING
)
@EqualsAndHashCode
public class Loan implements Serializable {
    private static final long serialVersionUID= 1L;

    @Id
    @Column(nullable = false,updatable = false)
    private String loanId;
    @Column(updatable = false,nullable = false,name = "borrower_id")
    private Long borrowerAccountNumber;
    private Long branchId;
    @Column(nullable = false)
    private String description;
    @Enumerated(EnumType.STRING)
    private LoanStatus status;
    @Column
    private boolean isFullyPaid;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "borrower_detail_id")
    private BorrowerDetails borrowerDetails;
    private Integer installmentCount;
    private Integer numberOfPayments;
    private BigDecimal monthlyInstallmentAmount;
    private BigDecimal interestToBePaid;
    private BigDecimal principalLoanAmount;
    private BigDecimal remainingPrincipal;
    @CreationTimestamp
    private LocalDate applicationDate;
    private LocalDate loanIssuedDate;
    private LocalDate dueDate;
    private LocalDate fullyPaidDate;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn
    private LoanRiskAnalysis loanRiskAnalysis;

    @OneToMany(mappedBy = "loan",cascade = {
            CascadeType.DETACH,CascadeType.PERSIST,CascadeType.MERGE,CascadeType.REFRESH
    }
    ,fetch = FetchType.LAZY
    )
    @JsonManagedReference
    @Fetch(FetchMode.SELECT)
    private Set<LoanPayments> payments;



    @Column(name = "loan_type",insertable = false,updatable = false)
    protected String loanType;


    /** 상환 기록을 추가하고 양방향 관계를 설정한다. */
    public void addPayments(LoanPayments payment){
            if (payment!=null){
                if (this.payments==null)
                    this.payments=new HashSet<>();
                this.payments.add(payment);
                payment.setLoan(this);
            }
    }

}
