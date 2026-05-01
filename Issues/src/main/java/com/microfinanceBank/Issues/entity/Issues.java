package com.microfinanceBank.Issues.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 불만사항(Issues) MongoDB 도큐먼트 엔티티.
 * 고객의 계좌 번호, 불만 내용, 처리 상태, 접수 날짜를 저장한다.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "issues")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Issues {

    /** 시퀀스 생성기를 통해 할당된 불만사항 ID (기본키). */
    @Id
    private Long id;
    /** 불만사항을 접수한 계좌 번호 (인덱스 적용). */
    @Indexed
    private Long accountNumber;
    /** 불만 내용. */
    private String issue;
    /** 처리 상태 (PENDING 또는 FIXED). */
    private IssueStatus status;
    /** 접수 날짜. */
    private LocalDate creationDate;
    /** 접수 시각. */
    private LocalDateTime time;

    /** MongoDB 시퀀스 컬렉션 이름. */
    @Transient
    public static final  String SEQUENCE_NAME="user_sequence";

  }
