package com.microfinanceBank.Customer.service.Impl;

import com.microfinanceBank.Customer.Exceptions.AccountNotActive;
import com.microfinanceBank.Customer.Exceptions.NoCustomerExceptions;
import com.microfinanceBank.Customer.Exceptions.InsufficientException;
import com.microfinanceBank.Customer.service.TransactionQueueProducerService;
import com.microfinanceBank.Customer.entity.Account;
import com.microfinanceBank.Customer.enums.Status;
import com.microfinanceBank.Customer.repository.AccountRepository;
import com.microfinanceBank.Customer.service.BankService;
import com.microfinanceBank.commondto.transaction.TransactionDto;
import com.microfinanceBank.commondto.transaction.TransactionStatus;
import com.microfinanceBank.commondto.transaction.TransferTransactionDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class BankServiceImpl implements BankService {
    private final AccountRepository accountRepository;
    private final TransactionQueueProducerService transactionQueueProducerService;


    @RabbitListener(queues = "MakingDepositQueue")
    @Override
    public void deposit(TransactionDto deposit)  {
        log.trace("entering method deposit");
        Account account = accountRepository.findById(deposit.getSourceAccount())
                .orElseThrow(()->new NoCustomerExceptions("입력하신 계좌 번호를 찾을 수 없습니다."));
        ;

        log.debug("making deposit for account number {}",deposit.getSourceAccount());

        var balance=account.getAccountBalance();

        if (isAccountNotActive(account)) {
            transactionQueueProducerService.deposit(deposit, TransactionStatus.ERROR);
            log.error("account number {} is inactive",account.getAccountNumber());
            throw new AccountNotActive("계좌가 비활성 상태입니다.");
        }
        account.setAccountBalance(balance.add(deposit.getAmount()));

        accountRepository.save(account);

        transactionQueueProducerService.deposit(deposit,TransactionStatus.SUCCESS);

        log.info("successfully deposit {} into account {}",deposit.getAmount(),account.getAccountNumber());
    }

    @Override
    public void withdraw(TransactionDto withdraw) {
         log.trace("entering method withdraw");
        var account=accountRepository.findById(withdraw.getSourceAccount())
                .orElseThrow(()->new NoCustomerExceptions("입력하신 계좌 번호를 찾을 수 없습니다."));
        System.out.println("making withdrawal for account number");
        log.debug("making withdrawal for account number {}",withdraw.getSourceAccount());

        var balance=account.getAccountBalance();

        if (isAccountNotActive(account)) {
            log.error("account number {} is inactive",account.getAccountNumber());
            throw new AccountNotActive("계좌가 비활성 상태입니다.");
        }
        if (hasNotSufficientFunds(balance,withdraw.getAmount())){
            transactionQueueProducerService.withdraw(withdraw,TransactionStatus.ERROR);
            log.error("insufficient funds to withdraw amount {} from {}",withdraw.getAmount(),withdraw.getSourceAccount());
            throw new InsufficientException("잔액이 부족합니다.");
        }
        account.setAccountBalance(balance.subtract(withdraw.getAmount()));
        accountRepository.save(account);

        transactionQueueProducerService.withdraw(withdraw,TransactionStatus.SUCCESS);

        log.info("{} successfully withdrawn from account {}",withdraw.getAmount(),account.getAccountNumber());
    }

    @Override
    @Transactional
    public void transfer(TransferTransactionDto transfer) {
        log.trace("entering method transfer");

        if (transfer.getRecipientAccount().equals(transfer.getSourceAccount())){
            log.error("You can not send money to yourself with account id {}",transfer.getSourceAccount());
            throw new IllegalStateException("본인 계좌로는 송금할 수 없습니다.");
        }

        var sender=accountRepository.findById(transfer.getSourceAccount())
                .orElseThrow(()->new NoCustomerExceptions("입력하신 출금 계좌 번호를 찾을 수 없습니다."));

        log.debug("making transfer for account number {}",transfer.getSourceAccount());

        var recipient=accountRepository.findById(transfer.getRecipientAccount())
                .orElseThrow(()->new NoCustomerExceptions("입력하신 수취 계좌 번호를 찾을 수 없습니다."));

        var senderAccountBalance=sender.getAccountBalance();

        var recipientAccountBalance=recipient.getAccountBalance();

        if (isAccountNotActive(sender)){
            transactionQueueProducerService.transfer(transfer,TransactionStatus.ERROR);
            log.error("sender with account number {} is inactive",recipient.getAccountNumber());
            throw new AccountNotActive("출금 계좌가 비활성 상태입니다.");
        }

        if (isAccountNotActive(recipient)){
            transactionQueueProducerService.transfer(transfer,TransactionStatus.ERROR);
            log.error("recipient with account number {} is inactive",recipient.getAccountNumber());
            throw new AccountNotActive("수취 계좌가 비활성 상태입니다.");
        }

        if (hasNotSufficientFunds(senderAccountBalance,transfer.getAmount())){
            transactionQueueProducerService.transfer(transfer,TransactionStatus.ERROR);
            log.error("insufficient funds to transfer amount {} from {} to {}",transfer.getAmount(),transfer.getSourceAccount(),transfer.getRecipientAccount());
            throw new InsufficientException("잔액이 부족합니다.");
        }

        sender.setAccountBalance(senderAccountBalance.subtract(transfer.getAmount()));
        recipient.setAccountBalance(recipientAccountBalance.add(transfer.getAmount()));
        accountRepository.saveAll(List.of(sender,recipient));


        transactionQueueProducerService.transfer(transfer,TransactionStatus.SUCCESS);

        log.info("{} successfully transferred from account {} to {}",transfer.getAmount(),transfer.getSourceAccount(),transfer.getRecipientAccount());

    }


    private boolean isAccountNotActive(Account account){
        return account.getStatus() != Status.ACTIVE;
    }
    private boolean hasNotSufficientFunds(BigDecimal balance,BigDecimal amount){
        return balance.compareTo(amount)<=0;
    }


}
