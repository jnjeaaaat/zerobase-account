package com.zerobase.account.service;

import com.zerobase.account.domain.Account;
import com.zerobase.account.domain.AccountUser;
import com.zerobase.account.domain.Transaction;
import com.zerobase.account.dto.TransactionDto;
import com.zerobase.account.exception.AccountException;
import com.zerobase.account.repository.AccountRepository;
import com.zerobase.account.repository.AccountUserRepository;
import com.zerobase.account.repository.TransactionRepository;
import com.zerobase.account.type.TransactionResultType;
import com.zerobase.account.type.TransactionType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

import static com.zerobase.account.type.AccountStatus.*;
import static com.zerobase.account.type.ErrorCode.*;
import static com.zerobase.account.type.TransactionType.*;
import static com.zerobase.account.type.TransactionResultType.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountUserRepository accountUserRepository;
    private final AccountRepository accountRepository;

    /**
     * 사용자가 없는 경우,
     * 사용자 아이디와 계좌 소유주가 다른 경우,
     * 계좌가 이미 해지 상태인 경우,
     * 거래 금액이 잔액보다 큰 경우,
     * 거래금액이 너무 작거나 큰 경우 실패 응답
     */
    @Transactional
    public TransactionDto useBalance(Long userId,
                                     String accountNumber,
                                     Long amount) {
        AccountUser accountUser = accountUserRepository.findById(userId)
                .orElseThrow(() -> new AccountException(USER_NOT_FOUND));
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountException(ACCOUNT_NOT_FOUND));

        validateUseBalance(accountUser, account, amount);

        account.useBalance(amount);

        return TransactionDto.fromEntity(
                saveAndGetTransaction(USE, S, account, amount)
        );
    }

    /**
     *  transaction validations
     */
    private void validateUseBalance(AccountUser accountUser, Account account, Long amount) {
        if (!Objects.equals(accountUser.getId(), account.getAccountUser().getId())) {
            throw new AccountException(USER_ACCOUNT_UN_MATCH);
        }
        if (account.getAccountStatus() != IN_USE) {
            throw new AccountException(ACCOUNT_ALREADY_UNREGISTERED);
        }
        if (account.getBalance() < amount) {
            throw new AccountException(AMOUNT_EXCEED_BALANCE);
        }
    }

    /**
     *  useBalance failed
     */
    @Transactional
    public void saveFailedUseTransaction(String accountNumber, Long amount) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountException(ACCOUNT_ALREADY_UNREGISTERED));

        saveAndGetTransaction(USE, F, account, amount);
    }

    private Transaction saveAndGetTransaction(
            TransactionType transactionType,
            TransactionResultType transactionResultType,
            Account account,
            Long amount) {
        return transactionRepository.save(
                Transaction.builder()
                        .transactionType(transactionType)
                        .transactionResultType(transactionResultType)
                        .account(account)
                        .amount(amount)
                        .balanceSnapshot(account.getBalance())
                        .transactionId(UUID.randomUUID().toString().replace("-", ""))
                        .transactedAt(LocalDateTime.now())
                        .build()
        );
    }

    @Transactional
    public TransactionDto cancelBalance(String transactionId,
                                        String accountNumber,
                                        Long amount) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountException(ACCOUNT_NOT_FOUND));
        Transaction transaction = transactionRepository.findByTransactionId(transactionId)
                .orElseThrow(() -> new AccountException(TRANSACTION_NOT_FOUND));

        validateCancelBalance(accountNumber, transaction, amount);

        account.cancelBalance(amount);

        return TransactionDto.fromEntity(
                saveAndGetTransaction(CANCEL, S, account, amount)
        );
    }

    private void validateCancelBalance(String accountNumber, Transaction transaction, Long amount) {
        if (!Objects.equals(accountNumber, transaction.getAccount().getAccountNumber())) {
            throw new AccountException(ACCOUNT_NUMBER_UN_MATCH);
        }
        if (!Objects.equals(amount, transaction.getAmount())) {
            throw new AccountException(AMOUNT_UN_MATCH);
        }
        if (transaction.getTransactedAt().isBefore(LocalDateTime.now().minusYears(1))) {
            throw new AccountException(TOO_OLD_ORDER_TO_CANCEL);
        }
    }

    @Transactional
    public void saveFailedCancelTransaction(String accountNumber, Long amount) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountException(ACCOUNT_ALREADY_UNREGISTERED));

        saveAndGetTransaction(CANCEL, F, account, amount);
    }
}
