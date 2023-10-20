package com.zerobase.account.controller;

import com.zerobase.account.config.BaseResponse;
import com.zerobase.account.domain.Account;
import com.zerobase.account.dto.AccountDto;
import com.zerobase.account.dto.AccountInfo;
import com.zerobase.account.dto.CreateAccount;
import com.zerobase.account.dto.DeleteAccount;
import com.zerobase.account.exception.AccountException;
import com.zerobase.account.service.AccountService;
import com.zerobase.account.service.RedisTestService;
import com.zerobase.account.type.AccountStatus;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static com.zerobase.account.type.SuccessCode.*;

@RestController
@RequiredArgsConstructor
public class AccountController {
    private final AccountService accountService;
    private final RedisTestService redisTestService;

    @PostMapping("/account")
    public BaseResponse<CreateAccount.Response> createAccount(
            @RequestBody @Valid CreateAccount.Request request) {

        try {
            return new BaseResponse<>(
                    SUCCESS_CREATE_ACCOUNT,
                    CreateAccount.Response.from(
                            accountService.createAccount(
                                    request.getUserId(),
                                    request.getInitialBalance()
                            )
                    ));
        } catch (AccountException exception) {
            return new BaseResponse<>(exception.getErrorCode());
        }

    }

    @DeleteMapping("/account")
    public BaseResponse<DeleteAccount.Response> deleteAccount(
            @RequestBody @Valid DeleteAccount.Request request) {

        try {
            return new BaseResponse<>(
                    SUCCESS_DELETE_ACCOUNT,
                    DeleteAccount.Response.from(
                            accountService.deleteAccount(
                                    request.getUserId(),
                                    request.getAccountNumber()
                            )
                    ));
        } catch (AccountException exception) {
            return new BaseResponse<>(exception.getErrorCode());
        }

    }

    @GetMapping("/account")
    public BaseResponse<List<AccountInfo>> getAccountsByUserId(
            @RequestParam("user_id") Long userId) {

        return new BaseResponse<>(
                SUCCESS_GET_ACCOUNT_LIST,
                accountService.getAccountsByUserId(userId, AccountStatus.IN_USE)
                        .stream()
                        .map(accountDto -> AccountInfo.builder()
                                .accountNumber(accountDto.getAccountNumber())
                                .balance(accountDto.getBalance())
                                .build())
                        .collect(Collectors.toList())
        );
    }

    @GetMapping("/lock")
    public String getLock() {
        return redisTestService.getLock();
    }

//    @GetMapping("/{accountId}")
//    public Account getAccount(@PathVariable Long accountId) {
//        return accountService.getAccount(accountId);
//    }

}
