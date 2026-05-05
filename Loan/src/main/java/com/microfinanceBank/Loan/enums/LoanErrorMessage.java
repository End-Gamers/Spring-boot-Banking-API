package com.microfinanceBank.Loan.enums;

import java.math.BigDecimal;
import java.math.RoundingMode;

/** 대출 서비스에서 사용하는 오류 메시지 열거형. */
public enum LoanErrorMessage {

    INTEREST_RATE_CANNOT_BE_NEGATIVE("이자율은 음수일 수 없습니다."
            , "interest_rate 상수 또는 API 값을 확인해 주세요."),
    PARAMETER_CANNOT_BE_NULL("필수 파라미터가 누락되었습니다."
            ,"모든 파라미터를 입력했는지 확인해 주세요."),
    INSTALLMENT_AMOUNT_MUST_BE_POSITIVE("할부 금액은 양수여야 합니다."
            ,"음수 금액이 입력되지 않았는지 확인해 주세요."),
    TOTAL_PAYMENT_MUST_BE_POSITIVE("총 상환액은 양수여야 합니다."
            ,"음수 대출 금액이 입력되지 않았는지 확인해 주세요."),
    LATE_FEE_RATE_CANNOT_BE_NEGATIVE("연체 이자율은 음수일 수 없습니다."
            ,"계산 결과가 올바르지 않습니다. 다시 확인해 주세요."),
    TOTAL_LATE_FEE_MUST_BE_POSITIVE("총 연체료는 양수여야 합니다."
            ,"유효한 양수 연체 일수를 입력했는지 확인해 주세요."),
    LATE_INTEREST_TAX_CANNOT_BE_NEGATIVE("연체 이자 세금은 음수일 수 없습니다."
            ,"음수 연체료 합계가 입력되지 않았는지 확인해 주세요."),
    LOAN_AMOUNT_CANNOT_BE_GREATER_THAN_MAX_AMOUNT("대출 금액이 최대 한도를 초과할 수 없습니다."
            ,"입력 가능한 최대 금액보다 큰 금액을 입력하셨습니다 (계산 기준):"),
    CUSTOMER_NOT_FOUND("고객을 찾을 수 없습니다."
            ,"대출의 고객 ID를 확인해 주세요."),
    MONTHLY_INSTALLMENT_AMOUNT_MUST_BE_POSITIVE("월 할부 금액은 양수여야 합니다."
            ,"할부 횟수를 양수로 입력했는지 확인해 주세요."),
    INTEREST_AMOUNT_CANNOT_BE_NEGATIVE("이자 금액은 음수일 수 없습니다."
            ,"계산 결과가 올바르지 않습니다. 다시 확인해 주세요."),
    PRINCIPAL_lOAN_AMOUNT_MUST_BE_POSITIVE("대출 원금은 양수여야 합니다."
            ,"대출 원금을 양수로 입력했는지 확인해 주세요."),
    LOAN_AMOUNT_NOT_ENOUGH_TO_PAY_OFF("상환 금액이 대출 잔액보다 부족합니다."
            ,"할부로 납부하거나 금액을 변경해 주세요. 잔여 상환 금액:"),
    DUE_DATE_HAS_NOT_PASSED_YET("아직 만기일이 도래하지 않았습니다."
            ,"만기일까지 남은 시간:"),
    REMAINING_PRINCIPAL_MUST_BE_POSITIVE("잔여 원금은 양수여야 합니다.",
            "계산 결과가 올바르지 않습니다. 다시 확인해 주세요." ),
    TAX_RATE_CANNOT_BE_NEGATIVE("세율은 음수일 수 없습니다."
            , "세율을 확인해 주세요." ),
    INSTALLMENT_COUNT_CANNOT_BE_LARGER_THAN_LIMIT("할부 횟수가 최대 한도를 초과할 수 없습니다."
            , "한도보다 작은 할부 횟수를 입력해 주세요:" ),
    LOAN_ALREADY_PAID_OFF("이미 상환 완료된 대출입니다."
            , "올바른 대출 ID를 입력했는지 확인해 주세요." ),
    ;

    private  final String message;
    private  String detailMessage;

    LoanErrorMessage(String message, String detailMessage){
        this.message = message;
        this.detailMessage = detailMessage;
    }

    public String getMessage() {
        return message;
    }

    public String getDetailMessage() {
        return detailMessage;
    }

    public void setDetailMessage(String extraDetail){
        detailMessage = detailMessage+" "+extraDetail;
    }

    public static void main(String[] args) {
        BigDecimal w= BigDecimal.valueOf(100.454465).divide(BigDecimal.valueOf(1), RoundingMode.UP);
        System.out.println(w.setScale(2,RoundingMode.UP));
    }
}
