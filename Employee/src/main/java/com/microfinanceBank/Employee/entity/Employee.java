package com.microfinanceBank.Employee.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.microfinanceBank.Employee.enums.Gender;
import com.microfinanceBank.Employee.enums.Role;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.keycloak.representations.idm.AdminEventRepresentation;
import org.keycloak.representations.idm.EventRepresentation;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
//import java.util.Collections;

/**
 * 직원(Employee) 엔티티.
 * 직원의 개인정보·역할·소속 지점·부서·활성 상태를 관리한다.
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Employee {
    /** 직원 ID (기본키). */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false,name = "emp_id",unique = true)
    private Long id;

    /** Keycloak에서 발급된 고유 사용자 ID. */
    @Column(updatable = false,unique = true,nullable = false)
    private String keycloakId;
    @Column(nullable = false)
    private String firstName;
    @Column(nullable = false)
    private String lastName;
    @ManyToOne(cascade ={
            CascadeType.DETACH,CascadeType.MERGE,CascadeType.PERSIST,CascadeType.REFRESH
    },fetch = FetchType.EAGER)
    @JoinColumn(name = "branch_id")
    @JsonBackReference
    private Branch branch;
    @Column(unique = true, nullable = false)
    private String email;
    @JoinColumn(name = "emp_details",unique = true)
    @OneToOne(cascade = CascadeType.ALL)
    private EmployeeDetails employeeDetails;
    @ManyToOne
    @JoinColumn(name = "dept_no")
    private Department dept;
    @Enumerated(EnumType.STRING)
    private Gender gender;
    @Enumerated(EnumType.STRING)
    private Role role;
    private String profileImageUrl;
    private Date lastLoginDate;
    private Date lastLoginDateDisplay;
    @CreationTimestamp
    private Date joinDate;
    private boolean isActive;
    private boolean isNotLocked;

    /** 직원을 지점에 배정하고 양방향 관계를 설정한다. */
    public void addBranch(Branch branch){
        if (branch !=null){
            this.branch=branch;
            branch.setEmployees(Collections.singletonList(this));
        }
    }
}

