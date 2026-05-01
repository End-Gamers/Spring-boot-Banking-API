package com.microfinanceBank.Employee.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

/**
 * 부서(Department) 엔티티.
 * 은행 내 부서 번호와 이름을 저장한다.
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Department implements Serializable {

    /** 부서 번호 (기본키, 고유). */
    @Id
    @Column(nullable = false,name = "dept_no",unique = true)
    private String deptNum;
    /** 부서 이름. */
    private String deptName;


}
