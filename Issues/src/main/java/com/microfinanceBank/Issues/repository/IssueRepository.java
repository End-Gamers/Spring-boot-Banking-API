package com.microfinanceBank.Issues.repository;

import com.microfinanceBank.Issues.entity.IssueStatus;
import com.microfinanceBank.Issues.entity.Issues;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;


/**
 * 불만사항(Issues) Reactive MongoDB 레포지토리.
 * 계좌 번호 및 처리 상태로 불만사항을 조회하는 쿼리 메서드를 제공한다.
 */
@Repository
public interface IssueRepository extends ReactiveCrudRepository<Issues,Long> {
    /** 특정 계좌 번호의 불만사항 목록을 반응형 스트림으로 반환한다. */
    Flux<Issues> findByAccountNumber(Long accountNumber);
    /** 특정 처리 상태의 불만사항 목록을 반응형 스트림으로 반환한다. */
    Flux<Issues> findByStatus(IssueStatus status);
}
