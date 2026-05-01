package com.microfinanceBank.Loan.entity;

import com.microfinanceBank.Loan.enums.MaritalStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;

/**
 * 차용자(BorrowerDetails) 상세 정보 엔티티.
 * 직업·이메일·월 수입·결혼 상태·자녀 수·생년월일 등 대출 심사 기준 정보를 저장한다.
 */
@Entity
@AllArgsConstructor
@Data
@NoArgsConstructor
public class BorrowerDetails implements Serializable {
    private static final long serialVersionUID= 1L;


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String occupation;
    private String email;
    private String mobileNumber;
    private double monthlyIncome;
    @Enumerated(EnumType.STRING)
    private MaritalStatus maritalStatus;
    private int children;
    private LocalDate dob;



}
