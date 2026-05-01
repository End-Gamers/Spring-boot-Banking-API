package com.microfinanceBank.Transaction.controller;



import com.microfinanceBank.Transaction.Service.TransactionService;
import com.microfinanceBank.Transaction.dto.*;
import com.microfinanceBank.Transaction.exceptions.AccountNotActive;
import com.microfinanceBank.Transaction.exceptions.InsufficientException;
import com.microfinanceBank.Transaction.exceptions.NoCustomerExceptions;
import com.microfinanceBank.Transaction.exceptions.ServiceUnavailableException;
import com.microfinanceBank.Transaction.exceptions.handler.ApiExceptionMessage;
import com.microfinanceBank.Transaction.exceptions.handler.ApplicationExceptionHandler;
import com.microfinanceBank.Transaction.projections.ITransaction;
import com.microfinanceBank.commondto.transaction.TransactionDto;
import com.microfinanceBank.commondto.transaction.TransferTransactionDto;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.ExceptionUtils;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Retryable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import javax.validation.Valid;
import java.net.ConnectException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static com.microfinanceBank.Transaction.Config.RabbitMQDirectConfig.MAKING_DEPOSIT_ROUTING_TRANSACTION;


/**
 * 거래(Transaction) REST 컨트롤러.
 * 입금(RabbitMQ 비동기), 출금·송금(WebFlux Reactive), 거래 내역 조회 API를 제공한다.
 */
@RestController
@RequestMapping("/api/")
@CrossOrigin(origins = "*",allowedHeaders = "*")
@RequiredArgsConstructor
@Slf4j
public class TransactionController {
    private final WebClient.Builder webClient;
    private final TransactionService transactionService;
    private  final DirectExchange directExchange;
    private  final AmqpTemplate amqpTemplate;
    private String baseUrl = "http://customer/api/transaction/";

    /**
     * 입금 요청을 RabbitMQ에 비동기로 발행한다.
     * @param transaction 입금 정보 DTO
     * @return 202 Accepted 응답
     */
    @PostMapping("deposit")
    public Mono<ResponseEntity> deposit(@Valid @RequestBody TransactionDto transaction) {

        amqpTemplate.convertAndSend(directExchange.getName(), MAKING_DEPOSIT_ROUTING_TRANSACTION, transaction);

        return Mono.just(new ResponseEntity<>(HttpStatus.ACCEPTED));
    }

    /**
     * 출금 요청을 Customer 서비스에 WebClient로 전달한다. 최대 3회 재시도한다.
     * @param transaction 출금 정보 DTO
     * @return Customer 서비스 응답
     */
    @PostMapping("withdraw")
    public Mono<ResponseEntity> withdraw(@Valid @RequestBody TransactionDto transaction){
        return webClient.build().post().uri(baseUrl.concat("withdraw"))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(transaction)
                .headers(httpHeaders -> httpHeaders.setBearerAuth(getTokenValue()))
                .retrieve()
                .onStatus(HttpStatus::isError,clientResponse ->
                        clientResponse
                        .bodyToMono(ApiExceptionMessage.class)
                        .flatMap(error-> {
                            if (error.getMessage().equals("Account not active"))
                                return Mono.error(new AccountNotActive(error.getMessage()));
                            if (error.getMessage().contains("No account with the provided"))
                                return Mono.error( new NoCustomerExceptions(error.getMessage()));
                            if (error.getMessage().contains("Insufficient Funds!!"))
                                return Mono.error( new InsufficientException(error.getMessage()));
                            return Mono.error(new RuntimeException(error.getMessage()));
                        }))
                .bodyToMono(ResponseEntity.class)

                .retryWhen(Retry.max(3)
                                .filter(throwable -> throwable.getMessage().equals("LoadBalancer does not contain an instance for the service customer"))
                        .onRetryExhaustedThrow((retrySpec, retrySignal) -> new ServiceUnavailableException(retrySignal.failure().getMessage())))
                .log();
    }
    /**
     * 송금 요청을 Customer 서비스에 WebClient로 전달한다. 최대 3회 재시도한다.
     * @param transfer 송금 정보 DTO
     * @return Customer 서비스 응답
     */
    @PostMapping("transfer")
    public Mono<ResponseEntity> transfer(@Valid @RequestBody TransferTransactionDto transfer){
        return webClient.build().post().uri(baseUrl.concat("transfer"))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(transfer)
                .headers(httpHeaders -> httpHeaders.setBearerAuth(getTokenValue()))
                .retrieve()
                .onStatus(HttpStatus::isError,clientResponse ->
                        clientResponse
                                .bodyToMono(ApiExceptionMessage.class)
                                .flatMap(error-> {
                                    if (error.getMessage().equals("Account not active"))
                                        return Mono.error(new AccountNotActive(error.getMessage()));
                                    if (error.getMessage().contains("No account with the provided"))
                                        return Mono.error( new NoCustomerExceptions(error.getMessage()));
                                    if (error.getMessage().contains("Insufficient Funds!!"))
                                        return Mono.error( new InsufficientException(error.getMessage()));
                                    return Mono.error(new RuntimeException(error.getMessage()));
                                }))
                .bodyToMono(ResponseEntity.class)
                .retryWhen(Retry.max(3)
                        .filter(throwable -> throwable.getMessage().equals("LoadBalancer does not contain an instance for the service customer"))
                        .onRetryExhaustedThrow((retrySpec, retrySignal) -> new ServiceUnavailableException(retrySignal.failure().getMessage())))
                .log();
    }

    /** 성공 처리된 모든 입금 거래 목록을 조회한다. */
    @GetMapping("all-success-deposit")
    public ResponseEntity<CompletableFuture<List<DepositDto>>> findAllSuccessfulDepositsTransactions() {
        var result=transactionService.findAllSuccessfulDepositsTransactions();
        return new ResponseEntity<>(result,HttpStatus.OK);
    }

    /** 실패 처리된 모든 입금 거래 목록을 조회한다. */
    @GetMapping("all-failed-deposit")
    public ResponseEntity<CompletableFuture<List<DepositDto>>> findAllFailedDepositTransactions() {
        var result=transactionService.findAllFailedDepositTransactions();
        return new ResponseEntity<>(result,HttpStatus.OK);
    }

    /** 특정 계좌의 모든 거래 내역을 페이지네이션하여 조회한다. */
    @GetMapping("all-customer-transaction")
    public ResponseEntity<CompletableFuture<List<ITransaction>>> allCustomerTransactions(@RequestParam("id") Long accountNum,@RequestParam("offset") int offset,@RequestParam("size") int size) {
        var result=transactionService.allCustomerTransactions(accountNum,offset,size);
        return new ResponseEntity<>(result,HttpStatus.OK);
    }

    /** SecurityContext에서 JWT Bearer 토큰 값을 추출한다. */
    private String getTokenValue(){
        var authentications = SecurityContextHolder.getContext().getAuthentication();
        var   principal= (Jwt)authentications.getPrincipal();
        return  principal.getTokenValue();
    }

}
