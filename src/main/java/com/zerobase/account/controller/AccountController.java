package com.zerobase.account.controller;

import com.zerobase.account.config.BaseResponse;
import com.zerobase.account.domain.Account;
import com.zerobase.account.dto.AccountDto;
import com.zerobase.account.dto.CreateAccount;
import com.zerobase.account.exception.AccountException;
import com.zerobase.account.service.AccountService;
import com.zerobase.account.service.RedisTestService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.zerobase.account.type.SuccessCode.*;

@RestController
@RequiredArgsConstructor
public class AccountController {
    private final AccountService accountService;
    private final RedisTestService redisTestService;

    @PostMapping("/account")
    public BaseResponse<CreateAccount.Response> createAccount(
            @RequestBody @Valid CreateAccount.Request createAccount) {

        try {
            return new BaseResponse<>(
                    SUCCESS_CREATE_ACCOUNT,
                    CreateAccount.Response.from(
                            accountService.createAccount(
                                    createAccount.getUserId(),
                                    createAccount.getInitialBalance()
                            )
                    ));
        } catch (AccountException exception) {
            return new BaseResponse<>(exception.getErrorCode());
        }

    }

    @GetMapping("/lock")
    public String getLock() {
        return redisTestService.getLock();
    }

    @GetMapping("/{accountId}")
    public Account getAccount(@PathVariable Long accountId) {
        return accountService.getAccount(accountId);
    }

}
