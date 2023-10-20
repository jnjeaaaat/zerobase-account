package com.zerobase.account.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SuccessCode {
    SUCCESS_CREATE_ACCOUNT(true, "새로운 계좌를 생성하였습니다.");

    private final boolean isSuccess;
    private final String description;
}

