package com.zerobase.account.controller;

import com.zerobase.account.domain.Account;
import com.zerobase.account.service.AccountService;
import com.zerobase.account.service.RedisTestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/app/accounts")
public class AccountController {
    private final AccountService accountService;
    private final RedisTestService redisTestService;

    @GetMapping("/lock")
    public String getLock() {
        return redisTestService.getLock();
    }

    @GetMapping("")
    public String createAccount() {
        accountService.createAccount();
        return "success";
    }

    @GetMapping("/{accountId}")
    public Account getAccount(@PathVariable Long accountId) {
        return accountService.getAccount(accountId);
    }

}
