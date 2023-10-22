package com.zerobase.account.service;

import com.zerobase.account.domain.Account;
import com.zerobase.account.domain.AccountUser;
import com.zerobase.account.domain.Transaction;
import com.zerobase.account.dto.TransactionDto;
import com.zerobase.account.exception.AccountException;
import com.zerobase.account.repository.AccountRepository;
import com.zerobase.account.repository.AccountUserRepository;
import com.zerobase.account.repository.TransactionRepository;
import com.zerobase.account.type.AccountStatus;
import com.zerobase.account.type.ErrorCode;
import com.zerobase.account.type.TransactionResultType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static com.zerobase.account.type.AccountStatus.IN_USE;
import static com.zerobase.account.type.TransactionResultType.*;
import static com.zerobase.account.type.TransactionType.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {
    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private AccountUserRepository accountUserRepository;

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private TransactionService transactionService;


    @Test
    void successUseBalance() {
        //given
        AccountUser user = AccountUser.builder()
                .id(12L)
                .name("Pobi").build();
        Account account = Account.builder()
                .accountUser(user)
                .accountStatus(IN_USE)
                .accountNumber("1000000012")
                .balance(10000L)
                .build();
        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.of(user));
        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.of(account));
        given(transactionRepository.save(any()))
                .willReturn(Transaction.builder()
                        .transactionType(USE)
                        .transactionResultType(S)
                        .account(account)
                        .amount(2000L)
                        .balanceSnapshot(8000L)
                        .transactionId("transactionId")
                        .transactedAt(LocalDateTime.now())
                        .build());

        //when
        transactionService.useBalance(1L,
                "1000000000", 1000L);

        ArgumentCaptor<Transaction> captor = ArgumentCaptor.forClass(Transaction.class);

        //then
        verify(transactionRepository, times(1)).save(captor.capture());

        assertEquals(USE, captor.getValue().getTransactionType());
        assertEquals(S, captor.getValue().getTransactionResultType());
        assertEquals("1000000012", captor.getValue().getAccount().getAccountNumber());
        assertEquals(1000L, captor.getValue().getAmount());
        assertEquals(9000L, captor.getValue().getBalanceSnapshot());
//        assertEquals("transactionId", captor.getValue().getTransactionId());
    }

    @Test
    @DisplayName("해당 유저 없음 - 잔액 사용 실패")
    void useBalance_UserNotFound() {
        //given
        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.empty());

        //when
        AccountException exception = assertThrows(AccountException.class,
                () -> transactionService.useBalance(1L, "1000000000", 1000L));

        //then
        assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    @DisplayName("해당 계좌 없음 - 잔액 사용 실패")
    void useBalance_AccountNotFound() {
        //given
        AccountUser user = AccountUser.builder()
                .id(15L).build();
        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.of(user));
        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.empty());

        //when
        AccountException exception = assertThrows(AccountException.class,
                () -> transactionService.useBalance(1L, "1000000000", 1000L));

        //then
        assertEquals(ErrorCode.ACCOUNT_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    @DisplayName("계좌 소유주 다름 - 잔액 사용 실패")
    void useBalance_userUnMatch() {
        //given
        AccountUser user = AccountUser.builder()
                .id(15L).build();
        AccountUser otherUser = AccountUser.builder()
                .id(13L).build();
        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.of(user));
        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.of(Account.builder()
                        .accountUser(otherUser)
                        .build()));

        //when
        AccountException exception = assertThrows(AccountException.class,
                () -> transactionService.useBalance(1L, "1000000000", 1000L));

        //then
        assertEquals(ErrorCode.USER_ACCOUNT_UN_MATCH, exception.getErrorCode());
    }

    @Test
    @DisplayName("이미 해지된 계좌 - 잔액 사용 실패")
    void useBalance_alreadyUnregistered() {
        //given
        AccountUser user = AccountUser.builder()
                .id(15L).build();
        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.of(user));
        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.of(Account.builder()
                        .accountUser(user)
                        .accountStatus(AccountStatus.UNREGISTERED)
                        .balance(0L)
                        .accountNumber("1000000012")
                        .build()));

        //when
        AccountException exception = assertThrows(AccountException.class,
                () -> transactionService.useBalance(1L, "1000000000", 1000L));

        //then
        assertEquals(ErrorCode.ACCOUNT_ALREADY_UNREGISTERED, exception.getErrorCode());
    }

    @Test
    @DisplayName("잔액 초과 - 잔액 사용 실패")
    void useBalance_amountExceedBalance() {
        //given
        AccountUser user = AccountUser.builder()
                .id(15L).build();
        Account account = Account.builder()
                .accountUser(user)
                .accountStatus(AccountStatus.IN_USE)
                .balance(1000L)
                .accountNumber("1000000012")
                .build();

        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.of(user));
        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.of(account));

        //when
        AccountException exception = assertThrows(AccountException.class,
                () -> transactionService.useBalance(1L, "1000000000", 3000L));

        //then
        assertEquals(ErrorCode.AMOUNT_EXCEED_BALANCE, exception.getErrorCode());
    }

    @Test
    @DisplayName("실패 트랜젝션 저장 성공")
    void saveFiledUseTransaction() {
        //given
        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.of(Account.builder()
                        .accountUser(AccountUser.builder()
                                .id(12L)
                                .name("Pobi").build())
                        .accountStatus(IN_USE)
                        .accountNumber("1000000012")
                        .balance(10000L)
                        .build()));

        ArgumentCaptor<Transaction> captor = ArgumentCaptor.forClass(Transaction.class);

        //when
        transactionService.saveFailedUseTransaction("1000000000", 1000L);

        //then
        verify(transactionRepository, times(1)).save(captor.capture());

        assertEquals(USE, captor.getValue().getTransactionType());
        assertEquals(F, captor.getValue().getTransactionResultType());
        assertEquals("1000000012", captor.getValue().getAccount().getAccountNumber());
        assertEquals(1000L, captor.getValue().getAmount());
        assertEquals(10000L, captor.getValue().getBalanceSnapshot());
//        assertEquals("transactionId", captor.getValue().getTransactionId());
    }

    @Test
    void successCancelUseBalance() {
        //given
        AccountUser user = AccountUser.builder()
                .id(12L)
                .name("Pobi").build();
        Account account = Account.builder()
                .accountUser(user)
                .accountStatus(IN_USE)
                .accountNumber("1000000012")
                .balance(10000L)
                .build();
        Transaction transaction = Transaction.builder()
                .transactionType(USE)
                .transactionResultType(S)
                .account(account)
                .amount(2000L)
                .balanceSnapshot(8000L)
                .transactionId("transactionId")
                .transactedAt(LocalDateTime.now())
                .build();

        given(transactionRepository.findByTransactionId(anyString()))
                .willReturn(Optional.of(transaction));
        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.of(account));
        given(transactionRepository.save(any()))
                .willReturn(Transaction.builder()
                        .transactionType(CANCEL)
                        .transactionResultType(S)
                        .account(account)
                        .amount(2000L)
                        .balanceSnapshot(10000L)
                        .transactionId("transactionIdForCancel")
                        .transactedAt(LocalDateTime.now())
                        .build());
        ArgumentCaptor<Transaction> captor = ArgumentCaptor.forClass(Transaction.class);

        //when
        TransactionDto transactionDto = transactionService.cancelBalance("transactionId",
                "1000000012", 2000L);

        //then
        verify(transactionRepository, times(1)).save(captor.capture());

        assertEquals(2000L, captor.getValue().getAmount());
        assertEquals(12000L, captor.getValue().getBalanceSnapshot());
        assertEquals(CANCEL, captor.getValue().getTransactionType());
        assertEquals(S, captor.getValue().getTransactionResultType());

        assertEquals(2000L, transactionDto.getAmount());
        assertEquals(10000L, transactionDto.getBalanceSnapshot());
    }

    @Test
    @DisplayName("해당 계좌 없음 - 잔액 사용 취소 실패")
    void cancelBalance_AccountNotFound() {
        //given
        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.empty());

        //when
        AccountException exception = assertThrows(AccountException.class,
                () -> transactionService.cancelBalance("transactionId",
                        "1000000000", 1000L));

        //then
        assertEquals(ErrorCode.ACCOUNT_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    @DisplayName("원 사용 거래 없음 - 잔액 사용 취소 실패")
    void cancelTransaction_TransactionNotFound() {
        //given
        AccountUser user = AccountUser.builder()
                .id(12L)
                .name("Pobi").build();
        Account account = Account.builder()
                .accountUser(user)
                .accountStatus(IN_USE)
                .accountNumber("1000000012")
                .balance(10000L)
                .build();
        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.of(account));
        given(transactionRepository.findByTransactionId(anyString()))
                .willReturn(Optional.empty());

        //when
        AccountException exception = assertThrows(AccountException.class,
                () -> transactionService.cancelBalance("transactionId", "1000000000", 1000L));

        //then
        assertEquals(ErrorCode.TRANSACTION_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    @DisplayName("계좌 다름 - 잔액 사용 취소 실패")
    void cancelTransaction_accountNumberUnMatch() {
        //given
        AccountUser user = AccountUser.builder()
                .id(12L)
                .name("Pobi").build();
        Account account = Account.builder()
                .accountUser(user)
                .accountStatus(IN_USE)
                .accountNumber("1000000012")
                .balance(10000L)
                .build();
        Account otherAccount = Account.builder()
                .accountUser(user)
                .accountStatus(IN_USE)
                .accountNumber("1000000013")
                .balance(10000L)
                .build();
        Transaction transaction = Transaction.builder()
                .transactionType(USE)
                .transactionResultType(S)
                .account(otherAccount)
                .amount(200L)
                .balanceSnapshot(9000L)
                .transactionId("transactionId")
                .transactedAt(LocalDateTime.now())
                .build();
        given(transactionRepository.findByTransactionId(anyString()))
                .willReturn(Optional.of(transaction));
        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.of(account));

        //when
        AccountException exception = assertThrows(AccountException.class,
                () -> transactionService.cancelBalance("transactionId", "1000000000", 1000L));

        //then
        assertEquals(ErrorCode.ACCOUNT_NUMBER_UN_MATCH, exception.getErrorCode());
    }

    @Test
    @DisplayName("거래금액과 취소금액이 다름 - 잔액 사용 취소 실패")
    void cancelTransaction_CancelMustFully() {
        //given
        AccountUser user = AccountUser.builder()
                .id(12L)
                .name("Pobi").build();
        Account account = Account.builder()
                .id(1L)
                .accountUser(user)
                .accountStatus(IN_USE)
                .balance(10000L)
                .accountNumber("1000000012")
                .build();
        Transaction transaction = Transaction.builder()
                .account(account)
                .transactionType(USE)
                .transactionResultType(S)
                .transactionId("transactionId")
                .transactedAt(LocalDateTime.now())
                .amount(200L + 1000L)
                .balanceSnapshot(9000L)
                .build();
        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.of(account));
        given(transactionRepository.findByTransactionId(anyString()))
                .willReturn(Optional.of(transaction));


        //when
        AccountException exception = assertThrows(AccountException.class,
                () -> transactionService
                        .cancelBalance(
                                "transactionId",
                                "1000000012",
                                200L
                        )
        );

        //then
        assertEquals(ErrorCode.AMOUNT_UN_MATCH, exception.getErrorCode());
    }

    @Test
    @DisplayName("1년이상 지난 거래 - 잔액 사용 취소 실패")
    void cancelTransaction_tooOldTransaction() {
        //given
        AccountUser user = AccountUser.builder()
                .id(12L)
                .name("Pobi").build();
        Account account = Account.builder()
                .id(1L)
                .accountUser(user)
                .accountStatus(IN_USE)
                .balance(10000L)
                .accountNumber("1000000012")
                .build();
        Transaction transaction = Transaction.builder()
                .account(account)
                .transactionType(USE)
                .transactionResultType(S)
                .transactionId("transactionId")
                .transactedAt(LocalDateTime.now().minusYears(2))
                .amount(1000L)
                .balanceSnapshot(9000L)
                .build();
        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.of(account));
        given(transactionRepository.findByTransactionId(anyString()))
                .willReturn(Optional.of(transaction));


        //when
        AccountException exception = assertThrows(AccountException.class,
                () -> transactionService
                        .cancelBalance(
                                "transactionId",
                                "1000000012",
                                1000L
                        )
        );

        //then
        assertEquals(ErrorCode.TOO_OLD_ORDER_TO_CANCEL, exception.getErrorCode());
    }
}