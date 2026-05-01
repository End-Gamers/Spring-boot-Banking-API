package com.microfinanceBank.Employee.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;
import org.hibernate.engine.internal.Cascade;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 은행 지점(Branch) 엔티티.
 * 지점에 소속된 직원 목록과 주소 정보를 관리한다.
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Branch  {
    /** 지점 ID (기본키). */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 해당 지점에 소속된 직원 목록. */
    @OneToMany(cascade=CascadeType.ALL,mappedBy = "branch",fetch = FetchType.EAGER )
    @JsonManagedReference
    private List<Employee> employees;
//    @ManyToOne
//    @JoinColumn(name = "manager_id")
//    private Employee manager;
    /** 지점 주소. */
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "address_id")
    private Address address;

    /** 지점 주소를 설정하고 양방향 관계를 구성한다. */
    public void addAddress(Address address) {
        this.address=address;
        address.setBranch(this);
    }

}
