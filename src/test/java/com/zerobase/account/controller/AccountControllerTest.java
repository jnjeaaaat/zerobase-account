package com.zerobase.account.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zerobase.account.domain.Account;
import com.zerobase.account.dto.AccountDto;
import com.zerobase.account.dto.CreateAccount;
import com.zerobase.account.dto.DeleteAccount;
import com.zerobase.account.type.AccountStatus;
import com.zerobase.account.service.AccountService;
import com.zerobase.account.service.RedisTestService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AccountController.class)
@AutoConfigureMockMvc
class AccountControllerTest {

    @MockBean
    private AccountService accountService;

    @MockBean
    private RedisTestService redisTestService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("계좌 생성 성공")
    void successCreateAccount() throws Exception {
        //given
        given(accountService.createAccount(anyLong(), anyLong()))
                .willReturn(AccountDto.builder()
                        .userId(1L)
                        .accountNumber("123456789")
                        .registeredAt(LocalDateTime.now())
                        .unRegisteredAt(LocalDateTime.now())
                        .build());

        //when
        //then
        mockMvc.perform(post("/account")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new CreateAccount.Request(1L, 100L)
                        )))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.userId").value(1))
                .andExpect(jsonPath("$.result.accountNumber").value("123456789"))
                .andDo(print());
    }

    @Test
    void successGetAccount() throws Exception {
        //given
        given(accountService.getAccount(anyLong()))
                .willReturn(Account.builder()
                        .accountNumber("3456")
                        .accountStatus(AccountStatus.IN_USE)
                        .build());

        //when


        //then
        mockMvc.perform(get("/app/accounts/876"))
                .andDo(print())
                .andExpect(jsonPath("$.result.accountNumber").value("3456"))
                .andExpect(jsonPath("$.result.accountStatus").value("IN_USE"))
                .andExpect(status().isOk());

    }

    @Test
    @DisplayName("계좌 해지 성공")
    void successDeleteAccount() throws Exception {
        //given
        given(accountService.deleteAccount(anyLong(), anyString()))
                .willReturn(AccountDto.builder()
                        .userId(1L)
                        .accountNumber("1234567890")
                        .registeredAt(LocalDateTime.now())
                        .unRegisteredAt(LocalDateTime.now())
                        .build());

        //when
        //then
        mockMvc.perform(delete("/account")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new DeleteAccount.Request(1L, "1234567890")
                        )))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.userId").value(1))
                .andExpect(jsonPath("$.result.accountNumber").value("1234567890"))
                .andDo(print());

    }

    @Test
    @DisplayName("계좌 조회 성공")
    void success_get_account() throws Exception {
        //given
        List<AccountDto> accountDtoList =
                Arrays.asList(
                        AccountDto.builder()
                                .accountNumber("1234567890")
                                .balance(1000L)
                                .build(),
                        AccountDto.builder()
                                .accountNumber("2345678901")
                                .balance(1000L)
                                .build(),
                        AccountDto.builder()
                                .accountNumber("3456789012")
                                .balance(1000L)
                                .build()
                );
        given(accountService.getAccountsByUserId(anyLong(), eq(AccountStatus.IN_USE)))
                .willReturn(accountDtoList);

        //when
        //then
        mockMvc.perform(get("/account?user_id=1"))
                .andExpect(jsonPath("$.result[0].accountNumber").value("1234567890"))
                .andExpect(jsonPath("$.result[0].balance").value(1000L))
                .andExpect(jsonPath("$.result[1].accountNumber").value("2345678901"))
                .andExpect(jsonPath("$.result[1].balance").value(1000L))
                .andExpect(jsonPath("$.result[2].accountNumber").value("3456789012"))
                .andExpect(jsonPath("$.result[2].balance").value(1000L))
                .andDo(print());
    }
}