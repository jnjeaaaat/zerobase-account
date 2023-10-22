package com.zerobase.account.dto;

import com.zerobase.account.type.ErrorCode;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ErrorResponse {
    private final Boolean isSuccess = false;
    private ErrorCode errorCode;
    private String errorMessage;
}
