package com.zerobase.account.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SuccessCode {
    SUCCESS_CREATE_ACCOUNT(200, "새로운 계좌를 생성하였습니다."),
    SUCCESS_DELETE_ACCOUNT(201, "계좌를 해지하였습니다."),
    SUCCESS_GET_ACCOUNT_LIST(202, "계좌 리스트를 조회하였습니다.")
    ;

    private final boolean isSuccess = true;
    private final int code;
    private final String description;
}

