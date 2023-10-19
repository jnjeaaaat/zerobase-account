package com.zerobase.account;

import com.zerobase.account.service.AccountService;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class AccountDtoTest {

    @Test
    void accountDto() {
        //given
        AccountDto accountDto = new AccountDto(
                "accountNumber",
                "summer",
                LocalDateTime.now()
        );

        //when
        String accountNum = accountDto.getAccountNumber();

        //then
        System.out.println(accountNum);
        System.out.println(accountDto.toString());

    }
}