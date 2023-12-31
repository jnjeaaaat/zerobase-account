package com.zerobase.account.dto;

import com.zerobase.account.aop.AccountLockIdInterface;
import com.zerobase.account.type.TransactionResultType;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;

public class CancelBalance {

    /**
     * {
     *    "transactionId":"abddfkdjfjdldk34dk",
     *    "accountNumber":"1000000000",
     * 	  "amount":1000
     * }
     */
    @Getter
    @Setter
    @AllArgsConstructor
    public static class Request implements AccountLockIdInterface {
        @NotBlank
        private String transactionId;

        @NotBlank
        @Size(min = 10, max = 10)
        private String accountNumber;

        @NotNull
        @Min(10)
        @Max(1000_000_000L)
        private Long amount;
    }

    /**
     * {
     *    "accountNumber":"1234567890",
     * 	  "transactionResult":"S",
     * 	  "transactionId":"c2033bb6d82a4250aecf8e27c49b63f6",
     * 	  "amount":1000,
     *    "transactedAt":"2022-06-01T23:26:14.671859"
     * }
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {
        private String accountNumber;
        private TransactionResultType transactionResultType;
        private String transactionId;
        private Long amount;
        private LocalDateTime transactedAt;

        public static Response from(TransactionDto transactionDto) {
            return Response.builder()
                    .accountNumber(transactionDto.getAccountNumber())
                    .transactionResultType(transactionDto.getTransactionResultType())
                    .transactionId(transactionDto.getTransactionId())
                    .amount(transactionDto.getAmount())
                    .transactedAt(transactionDto.getTransactedAt())
                    .build();
        }
    }
}
