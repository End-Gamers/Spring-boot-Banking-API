package com.microfinanceBank.Transaction.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * 거래 발생 위치 정보 엔티티.
 * 출금 거래의 ATM/가맹점 정보를 저장한다.
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Location {

    /** 위치 ID (기본키). */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    /** 가맹점 또는 ATM 제공업체명. */
    private String vendor;
    /** 거래 위치 주소 또는 설명. */
    private String location;

}
