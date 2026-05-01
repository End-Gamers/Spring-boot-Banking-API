package com.microfinanceBank.Transaction.Service.Impl;

import com.microfinanceBank.Transaction.Service.TransactionService;
import com.microfinanceBank.Transaction.dto.*;
import com.microfinanceBank.Transaction.entity.Transaction;
import com.microfinanceBank.Transaction.enums.TransactionType;
import com.microfinanceBank.Transaction.projections.ITransaction;
import com.microfinanceBank.Transaction.repository.DepositRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;


/**
 * TransactionService 구현체.
 * 거래 조회 로직을 비동기 CompletableFuture로 처리한다.
 */
@Service
@Slf4j
@AllArgsConstructor
public class TransactionServiceImpl implements TransactionService{

    private DepositRepository repository;
    private final ModelMapper modelMapper;


    Executor executor =Executors.newFixedThreadPool(10);

    /** 성공 처리된 모든 입금 거래를 비동기로 조회하여 DTO 목록으로 변환한다. */
    @Override
    @Transactional(readOnly = true)
    public CompletableFuture<List<DepositDto>> findAllSuccessfulDepositsTransactions() {
        log.trace("entering method findAllSuccessfulDepositsTransactions");
        log.debug("finding all deposit transactions");
        return CompletableFuture.supplyAsync(()-> {
            System.out.println("findAll thread "+Thread.currentThread().getName());
            return  repository
                    .findAllSuccessfulDepositsTransactions(TransactionType.DEPOSIT);},executor)
                .thenApplyAsync(convert->convert.stream()
                        .map(this::convertDepositEntityToDto)
                        .collect(Collectors.toList()), executor);
    }

    /** 실패 처리된 모든 입금 거래를 비동기로 조회하여 DTO 목록으로 변환한다. */
    @Override
    @Transactional(readOnly = true)
    public CompletableFuture<List<DepositDto>> findAllFailedDepositTransactions() {
        log.trace("entering method findAllFailedDepositTransactions");
        log.debug("finding all failed deposit transactions");
        return CompletableFuture.supplyAsync(()->{
             System.out.println("findAll thread "+Thread.currentThread().getName());
            return  repository.findAllFailedDepositTransactions(); }, executor)
                 .thenApplyAsync(data->data.stream().map(this::convertDepositEntityToDto).collect(Collectors.toList()), executor);
    }

    /** 특정 계좌의 모든 거래 내역을 페이지네이션하여 비동기로 조회한다. */
    @Override
    @Transactional(readOnly = true)
    public CompletableFuture<List<ITransaction>> allCustomerTransactions(Long accountNum,int offset, int size) {
        log.trace("entering method allCustomerTransactions");
        log.debug("finding all customer id {} transactions",accountNum);
        Pageable page= PageRequest.of(offset,size);
        return CompletableFuture
                .supplyAsync(()-> repository.findAllCustomersTransactions(accountNum,page),executor);
    }

    private DepositDto convertDepositEntityToDto(Transaction deposit){
        DepositDto depositDto;
        depositDto=modelMapper.map(deposit,DepositDto.class);
        return depositDto;
    }
    private DepositDto convertDepositEntityToDto(ITransaction deposit){
        DepositDto depositDto;
        depositDto=modelMapper.map(deposit,DepositDto.class);
        return depositDto;
    }





}
