package com.microfinanceBank.Transaction.enums;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;


/** 거래 종류를 나타내는 열거형. */
public enum  TransactionType {
    /** 출금. */
    WITHDRAW,
    /** 입금. */
    DEPOSIT,
    /** 송금. */
    TRANSFER
}
