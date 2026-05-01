package com.microfinanceBank.Loan.cronjobs;

import com.microfinanceBank.Loan.projections.AllDueDateLoan;
import com.microfinanceBank.Loan.repo.BankLoanRepository;
import com.microfinanceBank.commondto.CronJobQueueDto;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import static com.microfinanceBank.Loan.config.RabbitMQDirectConfig.LOAN_WITHDRAW_QUEUE;

/**
 * 은행 대출 자동 상환 크론 작업.
 * 매일 오전 11시에 만기된 대출을 조회하여 고객 계좌에서 자동으로 출금한다.
 * 연체 시 연체 이자를 계산하여 잔여 원금에 추가한다.
 */
@Component
@RequiredArgsConstructor
public class BankLoanJob {
    private final BankLoanRepository loanRepository;
    private final AmqpTemplate amqpTemplate;
    private final DirectExchange directExchange;

    Executor executor =Executors.newFixedThreadPool(10);

    /**
     * 매일 오전 11시에 실행되는 대출 자동 상환 작업.
     * 만기된 대출을 조회하고 연체 이자를 계산하여 RabbitMQ를 통해 출금을 요청한다.
     */
    @Scheduled(cron = "0 0 11 * * *")
    @Transactional
    public void bankLoanPaymentJob() {
        var allDueDateBankLoan=loanRepository.findAllDueDateBankLoan();
        allDueDateBankLoan.stream().forEach(loan->{
            var lastPayment=getLastPaymentDate(loan);
            var monthBehind=ChronoUnit.MONTHS.between(lastPayment,LocalDate.now());
                        var currentDebt=loan.getRemainingPrincipal();
                        var debtInterest= BigDecimal.ZERO;

                        //calculating late fee interest
                        if (monthBehind>0){
                            var interest= loan.getLoanOffer().getLatePaymentInterest()/100;
                            debtInterest= debtInterest.add(loan.getPrincipalLoanAmount()
                                    .multiply(BigDecimal.valueOf(interest)
                                    .multiply(BigDecimal.valueOf(monthBehind))));



                            var newPrincipal=loan.getRemainingPrincipal().add(debtInterest);

                            loanRepository.updateLoanPrincipalBalance(newPrincipal,loan.getLoanId());

                        }


                        var queue= CronJobQueueDto.builder()
                                .borrowerAccountNumber(loan.getBorrowerAccountNumber())
                                .currentDebt(currentDebt)
                                .debtInterest(debtInterest)
                                .loanId(loan.getLoanId())
                                .build();

                        //            async call to debit customer account

                        amqpTemplate.convertAndSend (directExchange.getName(), LOAN_WITHDRAW_QUEUE, queue);
                    });

        }

        /** 대출의 마지막 상환 날짜를 반환한다. 상환 기록이 없으면 대출 개시일을 반환한다. */
        private LocalDate getLastPaymentDate(AllDueDateLoan loan){
        // check if the customer has started paying
        if (loan.getPayments().isEmpty())
            return loan.getLoanIssuedDate();

        if (loan.getPayments().size()==1)
            return loan.getPayments().get(0).getPaymentDate();

        // sort the payment to get the last payment date
        return loan.getPayments().stream().sorted()
                    .collect(Collectors.toList()).get(loan.getPayments().size()-1).getPaymentDate();


        }

}
