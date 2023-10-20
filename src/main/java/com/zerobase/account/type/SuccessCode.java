package com.zerobase.account.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SuccessCode {
    SUCCESS_CREATE_ACCOUNT(200, "새로운 계좌를 생성하였습니다.");

    private final boolean isSuccess = true;
    private final int code;
    private final String description;
}

