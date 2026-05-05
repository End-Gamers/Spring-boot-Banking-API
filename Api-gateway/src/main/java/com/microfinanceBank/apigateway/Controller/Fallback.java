package com.microfinanceBank.apigateway.Controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * 서킷 브레이커(Circuit Breaker) 폴백 컨트롤러.
 * 각 마이크로서비스가 응답하지 않거나 타임아웃 발생 시 대체 응답을 반환한다.
 */
@RestController
public class Fallback {

    /** 고객 서비스 장애 시 503 응답을 반환한다. */
    @RequestMapping("/customerFallBack")
    public Mono<ResponseEntity<String>> customerServiceFallback(){

        return Mono.just(new ResponseEntity<>("고객 서비스가 응답하지 않거나 중단되었습니다. 잠시 후 다시 시도해 주세요.", HttpStatus.SERVICE_UNAVAILABLE));
    }

    /** 거래 서비스 장애 시 503 응답을 반환한다. */
    @RequestMapping("/transactionFallBack")
    public Mono<ResponseEntity<String>> transactionServiceFallback(){
        return Mono.just(new ResponseEntity<>("거래 서비스가 응답하지 않거나 중단되었습니다. 잠시 후 다시 시도해 주세요.", HttpStatus.SERVICE_UNAVAILABLE));
    }

    /** 직원 서비스 장애 시 503 응답을 반환한다. */
    @RequestMapping("/employeeFallBack")
    public Mono<ResponseEntity<String>> userServiceFallback(){
        return Mono.just(new ResponseEntity<>("직원 서비스가 응답하지 않거나 중단되었습니다. 잠시 후 다시 시도해 주세요.", HttpStatus.SERVICE_UNAVAILABLE));
    }

    /** 불만사항 서비스 장애 시 503 응답을 반환한다. */
    @RequestMapping("/issueFallBack")
    public Mono<ResponseEntity<String>> issueServiceFallback(){
        return Mono.just(new ResponseEntity<>("불만사항 서비스가 응답하지 않거나 중단되었습니다. 잠시 후 다시 시도해 주세요.", HttpStatus.SERVICE_UNAVAILABLE));
    }

    /** 대출 서비스 장애 시 503 응답을 반환한다. */
    @RequestMapping("/loanFallBack")
    public Mono<ResponseEntity<String>> loanServiceFallback(){
        return Mono.just(new ResponseEntity<>("대출 서비스가 응답하지 않거나 중단되었습니다. 잠시 후 다시 시도해 주세요.", HttpStatus.SERVICE_UNAVAILABLE));
    }
}
