package com.microfinanceBank.Loan.controller;

import com.microfinanceBank.Loan.dto.MakePaymentDto;
import com.microfinanceBank.Loan.service.Payment;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * 대출 상환 처리 REST 컨트롤러.
 * 대출 원금 차감 및 상환 기록 저장 API를 제공한다.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("api")
@SecurityRequirement(name = "Bearer Authentication")
public class MakePaymentController {

    private final Payment payment;

    /** 대출 상환금을 처리하고 잔여 원금을 갱신한다. */
    @PostMapping("payment-loan")
    public ResponseEntity paymentLoan(@Valid @RequestBody MakePaymentDto makePaymentDto){
        payment.makeLoanPayment(makePaymentDto);

        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }
}
