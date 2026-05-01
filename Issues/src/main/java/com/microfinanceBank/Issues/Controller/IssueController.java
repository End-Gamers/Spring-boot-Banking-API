package com.microfinanceBank.Issues.Controller;

import com.microfinanceBank.Issues.Service.IssueService;
import com.microfinanceBank.Issues.dto.ComplainDto;
import com.microfinanceBank.Issues.dto.IssueDto;
import com.microfinanceBank.Issues.dto.IssueResponse;
import com.microfinanceBank.Issues.repository.IssueRepository;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.util.List;

/**
 * 불만사항(Issue) 관리 REST 컨트롤러.
 * 불만 접수·조회·해결 API를 제공하며 Reactive 스트림(Mono/Flux)을 사용한다.
 */
@RestController
@AllArgsConstructor
@RequestMapping("api")
@SecurityRequirement(name = "Bearer Authentication")
public class IssueController {
    private final IssueService issueService;

    /** 새 불만사항을 접수한다. */
    @PostMapping("issue")
    public ResponseEntity<Mono<IssueResponse>> makeComplaint(@Valid @RequestBody ComplainDto complainDto) {
        return new ResponseEntity<>(issueService.makeComplaint(complainDto), HttpStatus.CREATED);
    }

    /** 특정 계좌 번호의 불만사항 목록을 조회한다. */
    @GetMapping("issue")
    public ResponseEntity<Flux<IssueDto>> getIssuesByAccountNumber(@RequestParam("acc") Long accountNumber) {
        return new ResponseEntity<>(issueService.getIssuesByAccountNumber(accountNumber),HttpStatus.OK);
    }

    /** 모든 불만사항을 SSE(Server-Sent Events) 스트림으로 조회한다. */
    @GetMapping( path = "all-issues",produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseEntity<Flux<IssueDto>> getAllIssues() {
        return new ResponseEntity<>(issueService.getAllIssues(),HttpStatus.OK);
    }

    /** 처리 대기(PENDING) 상태의 불만사항 목록을 조회한다. */
    @GetMapping("pending-issues")
    public ResponseEntity<Flux<IssueDto>> getAllPendingIssues() {
        return new ResponseEntity<>(issueService.getAllPendingIssues(),HttpStatus.OK);
    }

    /** 특정 불만사항을 해결 완료(FIXED) 상태로 변경한다. */
    @PutMapping("issue-fix")
    public ResponseEntity<Mono<IssueResponse>> fixIssue(@RequestParam Long id){

        return new ResponseEntity<>(issueService.fixIssue(id),HttpStatus.ACCEPTED) ;
    }

}
