package com.zerobase.account.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    USER_NOT_FOUND(400, "사용자가 없습니다."),
    MAX_ACCOUNT_PER_USER_10(401, "사용자 최대 계좌는 10개입니다."),
    ACCOUNT_NOT_FOUND(402, "계좌가 없습니다."),
    USER_ACCOUNT_UN_MATCH(403, "사용자와 계좌의 소유주가 다릅니다."),
    ACCOUNT_ALREADY_UNREGISTERED(404, "계좌가 이미 해지되었습니다."),
    BALANCE_NOT_EMPTY(405, "잔액이 있는 계좌는 해지할 수 없습니다."),
    AMOUNT_EXCEED_BALANCE(406, "거래 금액이 계좌 잔액보다 큽니다.")
    ;


    private final boolean isSuccess = false;
    private final int code;
    private final String description;
}
