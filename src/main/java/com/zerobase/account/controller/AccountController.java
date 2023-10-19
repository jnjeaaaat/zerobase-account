package com.zerobase.account.controller;

import com.zerobase.account.domain.Account;
import com.zerobase.account.dto.AccountDto;
import com.zerobase.account.dto.CreateAccount;
import com.zerobase.account.service.AccountService;
import com.zerobase.account.service.RedisTestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class AccountController {
    private final AccountService accountService;
    private final RedisTestService redisTestService;

    @PostMapping("/account")
    public CreateAccount.Response createAccount(
            @RequestBody @Valid CreateAccount.Request createAccount) {

        return CreateAccount.Response.from(
                accountService.createAccount(
                        createAccount.getUserId(),
                        createAccount.getInitialBalance()
                )
        );
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
