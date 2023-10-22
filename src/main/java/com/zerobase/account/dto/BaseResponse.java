package com.zerobase.account.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.zerobase.account.type.ErrorCode;
import com.zerobase.account.type.SuccessCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@JsonPropertyOrder({"isSuccess", "successCode", "message", "result"})
public class BaseResponse<T> {//BaseResponse 객체를 사용할때 성공, 실패 경우
    @JsonProperty("isSuccess")
    private final Boolean isSuccess = true;
    private final SuccessCode successCode;
    private final String message;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T result;

    public BaseResponse(SuccessCode successCode, T result) {
        this.successCode = successCode;
        this.message = successCode.getDescription();
        this.result = result;
    }
}
