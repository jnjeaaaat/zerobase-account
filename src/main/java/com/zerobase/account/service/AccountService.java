package com.zerobase.account.service;

import com.zerobase.account.domain.Account;
import com.zerobase.account.domain.AccountUser;
import com.zerobase.account.dto.AccountDto;
import com.zerobase.account.dto.AccountInfo;
import com.zerobase.account.exception.AccountException;
import com.zerobase.account.repository.AccountRepository;
import com.zerobase.account.repository.AccountUserRepository;
import com.zerobase.account.type.AccountStatus;
import com.zerobase.account.type.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.zerobase.account.type.AccountStatus.*;
import static com.zerobase.account.type.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;
    private final AccountUserRepository accountUserRepository;

    /**
     * 사용자가 있는지 조회
     * 계좌의 번호를 생성하고
     * 계좌를 저장하고, 그 정보를 넘긴다.
     */
    @Transactional
    public AccountDto createAccount(Long userId, Long initialBalance) {
        AccountUser accountUser = getAccountUser(userId);

        validateCreateAccount(accountUser);

        String newAccountNumber = accountRepository.findFirstByOrderByIdDesc()
                .map(account -> (Integer.parseInt(account.getAccountNumber())) + 1 + "")
                .orElse("1000000000");
        // orElse(): 없으면 default 값 설정

        return AccountDto.fromEntity(
                accountRepository.save(
                        Account.builder()
                                .accountUser(accountUser)
                                .accountNumber(newAccountNumber)
                                .accountStatus(IN_USE)
                                .balance(initialBalance)
                                .registeredAt(LocalDateTime.now())
                                .build()
                )
        );
    }

    private void validateCreateAccount(AccountUser accountUser) {
        if (accountRepository.countByAccountUser(accountUser) >= 10) {
            throw new AccountException(MAX_ACCOUNT_PER_USER_10);
        }
    }

    @Transactional
    public Account getAccount(Long id) {
        if (id < 0) {
            throw new RuntimeException("Minus");
        }
        return accountRepository.findById(id).get();
    }

    @Transactional
    public AccountDto deleteAccount(Long userId, String accountNumber) {
        AccountUser accountUser = getAccountUser(userId);
        // orElseThrow(): 있으면 데이터 저장, 없으면 error

        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountException(ACCOUNT_NOT_FOUND));

        validateDeleteAccount(accountUser, account);

        account.setAccountStatus(UNREGISTERED); // 계좌 상태 변경
        account.setUnRegisteredAt(LocalDateTime.now()); // 해지 날짜 등록

        accountRepository.save(account); // test verify() 를 위한 save() method

        return AccountDto.fromEntity(account);
    }

    private void validateDeleteAccount(AccountUser accountUser, Account account) {
        // 유저 아이디와, 계좌 소유주가 다를 때
        if (!Objects.equals(accountUser.getId(), account.getAccountUser().getId())) {
            throw new AccountException(USER_ACCOUNT_UN_MATCH);
        }

        // 이미 해지된 계좌 일 때
        if (account.getAccountStatus() == UNREGISTERED) {
            throw new AccountException(ACCOUNT_ALREADY_UNREGISTERED);
        }

        // 해지할 계좌의 금액이 0원이 아닐 때
        if (account.getBalance() > 0) {
            throw new AccountException(BALANCE_NOT_EMPTY);
        }
    }

    @Transactional
    public List<AccountDto> getAccountsByUserId(Long userId,
                                                AccountStatus accountStatus) {
        AccountUser accountUser = getAccountUser(userId);

        List<Account> account = accountRepository
                .findByAccountUserAndAccountStatus(accountUser, accountStatus);

        return account.stream()
                .map(AccountDto::fromEntity)
                .collect(Collectors.toList());
    }

    private AccountUser getAccountUser(Long userId) {
        AccountUser accountUser = accountUserRepository.findById(userId)
                .orElseThrow(() -> new AccountException(USER_NOT_FOUND));
        // orElseThrow(): 있으면 데이터 저장, 없으면 error

        return accountUser;
    }
}
