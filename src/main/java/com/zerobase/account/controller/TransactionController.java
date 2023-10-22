package com.zerobase.account.controller;

import com.zerobase.account.dto.*;
import com.zerobase.account.exception.AccountException;
import com.zerobase.account.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import static com.zerobase.account.type.SuccessCode.*;

/**
 * 잔액 관련 컨트롤러
 * 1. 잔액 사용
 * 2. 잔액 사용 취소
 * 3. 거래 확인
 */

@Slf4j
@RestController
@RequiredArgsConstructor
public class TransactionController {
    private final TransactionService transactionService;

    /** 잔액 사용 transaction */
    @PostMapping("/transaction/use")
    public BaseResponse<UseBalance.Response> useBalance(
            @Valid @RequestBody UseBalance.Request request) {

        try {
            return new BaseResponse<>(
                    SUCCESS_USE_BALANCE,
                    UseBalance.Response.from(
                            transactionService.useBalance(
                                    request.getUserId(),
                                    request.getAccountNumber(),
                                    request.getAmount()
                            )
                    )
            );
        } catch (AccountException e) {
            log.error("Failed to use balance.");

            transactionService.saveFailedUseTransaction(
                    request.getAccountNumber(),
                    request.getAmount()
            );

            throw e;
        }

    }

    /** 잔액 사용 취소 transaction */
    @PostMapping("/transaction/cancel")
    public BaseResponse<CancelBalance.Response> cancelBalance(
            @Valid @RequestBody CancelBalance.Request request) {

        try {
            return new BaseResponse<>(
                    SUCCESS_CANCEL_BALANCE,
                    CancelBalance.Response.from(
                            transactionService.cancelBalance(
                                    request.getTransactionId(),
                                    request.getAccountNumber(),
                                    request.getAmount()
                            )
                    )
            );
        } catch (AccountException e) {
            log.error("Failed to use balance.");

            transactionService.saveFailedCancelTransaction(
                    request.getAccountNumber(),
                    request.getAmount()
            );

            throw e;
        }
    }

    /** transactionId로 transaction 조회 */
    @GetMapping("/transaction/{transactionId}")
    public BaseResponse<QueryTransactionResponse> queryTransaction(
            @PathVariable String transactionId) {
        return new BaseResponse<>(
                SUCCESS_GET_TRANSACTION,
                QueryTransactionResponse.from(
                        transactionService.queryTransaction(transactionId)
                )
        );
    }

}
