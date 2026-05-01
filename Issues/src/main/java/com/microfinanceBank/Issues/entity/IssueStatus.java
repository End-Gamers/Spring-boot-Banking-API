package com.microfinanceBank.Issues.entity;


import lombok.Getter;

/** 불만사항 처리 상태를 나타내는 열거형. */
public enum IssueStatus {
    /** 처리 대기 중. */
    PENDING,
    /** 해결 완료. */
    FIXED

}

