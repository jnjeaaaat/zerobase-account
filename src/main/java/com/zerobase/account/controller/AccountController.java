package com.zerobase.account.controller;

import com.zerobase.account.domain.Account;
import com.zerobase.account.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/app/accounts")
public class AccountController {
    private final AccountService accountService;

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
