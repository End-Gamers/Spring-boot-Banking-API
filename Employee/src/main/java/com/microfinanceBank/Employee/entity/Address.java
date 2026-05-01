package com.microfinanceBank.Employee.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;


/**
 * 주소(Address) 엔티티.
 * 은행 지점의 상세 위치 정보를 저장한다.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
public class Address {
    /** 주소 ID (기본키). */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    /** 도로명 주소. */
    private String street;
    /** 도시. */
    private String city;
    /** 주(State). */
    private String state;
    /** 국가. */
    private String country;
    /** 우편번호. */
    private String postalCode;
    /** 이 주소를 가진 지점 (역방향 참조). */
    @OneToOne(mappedBy = "address")
    @JsonBackReference
    private Branch branch;
}
