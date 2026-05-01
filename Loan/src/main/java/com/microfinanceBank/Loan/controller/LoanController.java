package com.microfinanceBank.Loan.controller;

import com.microfinanceBank.Loan.cronjobs.BankLoanJob;
import com.microfinanceBank.Loan.dto.LoanRequest;
import com.microfinanceBank.Loan.dto.LoanRequestResponse;
import com.microfinanceBank.Loan.service.LoanService;
import com.microfinanceBank.Loan.service.P2pService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 대출 관리 REST 컨트롤러.
 * 은행 대출 신청·P2P 대출 신청·대출 승인 API를 제공한다.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("api")
public class LoanController {

    private final LoanService loanService;
    private  final P2pService p2pService;

    /** 은행 대출을 신청한다. 신청 즉시 위험도 분석이 RabbitMQ를 통해 비동기 처리된다. */
    @PostMapping("bank-loan")
    public ResponseEntity<LoanRequestResponse> BankLoanRequest(@Valid @RequestBody LoanRequest loanRequest){
        var response=loanService.loanRequest(loanRequest);

        return new ResponseEntity<>(response,HttpStatus.ACCEPTED);
    }

    /** P2P 대출을 신청한다. */
    @PostMapping("p2p-loan")
    public ResponseEntity P2pRequestLoan(@Valid @RequestBody LoanRequest loanRequest){
        p2pService.loanRequest(loanRequest);

        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    /** 대출 신청을 승인하고 대출 개시일 및 만기일을 설정한다. ADMIN 전용 기능이다. */
    @PutMapping("approve-loan")
    public ResponseEntity approveLoans(@RequestParam("loan-id") String id){
        loanService.approveLoan(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }



}
