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
    public void createAccount() {
        Account account = Account.builder()
                .accountNumber("40000")
                .accountStatus(AccountStatus.IN_USE)
                .build();
        accountRepository.save(account);
    }

    @Transactional
    public Account getAccount(Long accountId) {
        return accountRepository.findById(accountId).get();
    }
}
