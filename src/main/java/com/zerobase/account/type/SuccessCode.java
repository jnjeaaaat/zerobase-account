package com.zerobase.account.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SuccessCode {
    SUCCESS_CREATE_ACCOUNT("새로운 계좌를 생성하였습니다."),
    SUCCESS_DELETE_ACCOUNT("계좌를 해지하였습니다."),
    SUCCESS_GET_ACCOUNT_LIST("계좌 리스트를 조회하였습니다."),
    SUCCESS_USE_BALANCE("잔액을 사용하였습니다."),
    SUCCESS_CANCEL_BALANCE("잔액 사용을 취소하였습니다."),
    SUCCESS_GET_TRANSACTION("해당 거래를 조회하였습니다.");

    private final String description;
}

