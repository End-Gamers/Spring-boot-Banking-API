package com.microfinanceBank.Transaction.Listener;

import com.microfinanceBank.Transaction.repository.TransactionRepository;
import com.microfinanceBank.Transaction.entity.Deposit;
import com.microfinanceBank.Transaction.entity.TransactionDetail;
import com.microfinanceBank.Transaction.entity.Transfer;
import com.microfinanceBank.Transaction.entity.Withdraw;
import com.microfinanceBank.commondto.transaction.DepositQueue;
import com.microfinanceBank.commondto.transaction.TransactionDetailsDto;
import com.microfinanceBank.commondto.transaction.TransferQueue;
import com.microfinanceBank.commondto.transaction.WithdrawQueue;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * RabbitMQ를 통해 거래 메시지를 수신하는 리스너.
 * DepositQueue·WithdrawalQueue·TransferQueue를 구독하여 거래 데이터를 DB에 저장한다.
 */
@Component
@RequiredArgsConstructor
//@Transactional
public class CustomerServiceTransactionListener {
    private final TransactionRepository depositRepository;
    private final TransactionRepository withdrawalRepository;
    private final TransactionRepository transferRepository;
    private final ModelMapper modelMapper;

    /** DepositQueue에서 입금 메시지를 수신하여 DB에 저장한다. */
    @RabbitListener(queues = "DepositQueue")
    public void depositQueue(DepositQueue depositQueue){
        depositRepository.save(convertDepositQueueToEntity(depositQueue));
    }

    /** WithdrawalQueue에서 출금 메시지를 수신하여 DB에 저장한다. */
    @RabbitListener(queues = "WithdrawalQueue")
    public void withdrawQueue(WithdrawQueue withdrawQueue){

        withdrawalRepository.save(convertWithdrawQueueToEntity(withdrawQueue));
    }

    /** TransferQueue에서 송금 메시지를 수신하여 DB에 저장한다. */
    @RabbitListener(queues = "TransferQueue")
    public void transferQueue(TransferQueue transferQueue){
        transferRepository.save(convertTransferQueueToEntity(transferQueue));
    }

    /** DepositQueue 메시지를 Deposit 엔티티로 변환한다. */
    private Deposit convertDepositQueueToEntity(DepositQueue depositQueue){
        var deposit=new Deposit();
        deposit=modelMapper.map(depositQueue.getDeposit(),Deposit.class);
        deposit.setTransactionDetail(convertTransactionDetailToEntity(depositQueue.getTransactionDetails()));
        return deposit;
    }
    /** WithdrawalQueue 메시지를 Withdraw 엔티티로 변환한다. */
    private Withdraw convertWithdrawQueueToEntity(WithdrawQueue withdrawDto){
        var withdraw=new Withdraw();
        withdraw=modelMapper.map(withdrawDto.getWithdraw(),Withdraw.class);
        withdraw.setTransactionDetail(convertTransactionDetailToEntity(withdrawDto.getTransactionDetails()));
        return withdraw;
    }
    /** TransferQueue 메시지를 Transfer 엔티티로 변환한다. */
    private Transfer convertTransferQueueToEntity(TransferQueue transferDto){
        var transfer=new Transfer();
        transfer=modelMapper.map(transferDto.getTransfer(),Transfer.class);
        transfer.setTransactionDetail(convertTransactionDetailToEntity(transferDto.getTransactionDetails()));

        return transfer;
    }

    /** TransactionDetailsDto를 TransactionDetail 엔티티로 변환한다. */
    private TransactionDetail convertTransactionDetailToEntity(TransactionDetailsDto detailsDto){
        var details=new TransactionDetail();
        details=modelMapper.map(detailsDto,TransactionDetail.class);
        return details;

    }

}
