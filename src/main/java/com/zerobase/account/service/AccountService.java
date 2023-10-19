package com.zerobase.account.service;

import com.zerobase.account.domain.Account;
import com.zerobase.account.domain.AccountStatus;
import com.zerobase.account.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;

    @Transactional
    public void createAccount(Long userId, Long initialBalance) {

    }

    @Transactional
    public Account getAccount(Long accountId) {
        if (accountId < 0) {
            throw new RuntimeException("Minus");
        }
        return accountRepository.findById(accountId).get();
    }
}
