package com.zerobase.account.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.zerobase.account.type.ErrorCode;
import com.zerobase.account.type.SuccessCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@JsonPropertyOrder({"isSuccess", "code", "message", "result"})
public class BaseResponse<T> {//BaseResponse 객체를 사용할때 성공, 실패 경우
    @JsonProperty("isSuccess")
    private final Boolean isSuccess;
    private final String message;
    private final int code;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T result;

    // 요청에 실패한 경우
    public BaseResponse(ErrorCode errorCode) {
        this.isSuccess = errorCode.isSuccess();
        this.message = errorCode.getDescription();
        this.code = errorCode.getCode();
    }

    public BaseResponse(SuccessCode successCode, T result) {
        this.isSuccess = successCode.isSuccess();
        this.message = successCode.getDescription();
        this.code = successCode.getCode();
        this.result = result;
    }
}
