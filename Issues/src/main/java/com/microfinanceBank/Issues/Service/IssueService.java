package com.microfinanceBank.Issues.Service;

import com.microfinanceBank.Issues.dto.ComplainDto;
import com.microfinanceBank.Issues.dto.IssueDto;
import com.microfinanceBank.Issues.dto.IssueResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * 불만사항 관리 서비스 인터페이스.
 * 불만 접수·조회·해결 기능을 Reactive 스트림으로 정의한다.
 */
public interface IssueService {
    /** 고객의 불만사항을 접수한다. */
    Mono<IssueResponse> makeComplaint(ComplainDto complainDto);
    /** 특정 계좌 번호의 불만사항 목록을 조회한다. */
    Flux<IssueDto> getIssuesByAccountNumber(Long accountNumber);
    /** 전체 불만사항 목록을 조회한다. */
    Flux<IssueDto> getAllIssues();
    /** 특정 불만사항을 해결 처리한다. */
    Mono<IssueResponse> fixIssue(Long id);
    /** 처리 대기 중인 불만사항 목록을 조회한다. */
    Flux<IssueDto> getAllPendingIssues();
}
