package com.zerobase.account.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zerobase.account.domain.Account;
import com.zerobase.account.domain.Transaction;
import com.zerobase.account.dto.CancelBalance;
import com.zerobase.account.dto.TransactionDto;
import com.zerobase.account.dto.UseBalance;
import com.zerobase.account.service.TransactionService;
import com.zerobase.account.type.TransactionResultType;
import com.zerobase.account.type.TransactionType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TransactionController.class)
class TransactionControllerTest {
    @MockBean
    private TransactionService transactionService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("잔액 사용 성공")
    void use_balance_success() throws Exception {
        //given
        given(transactionService.useBalance(anyLong(), anyString(), anyLong()))
                .willReturn(TransactionDto.builder()
                        .accountNumber("1234567890")
                        .transactionResultType(TransactionResultType.S)
                        .transactionId("transactionId")
                        .amount(12345L)
                        .transactedAt(LocalDateTime.now())
                        .build()
                );

        //when
        //then

        mockMvc.perform(post("/transaction/use")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new UseBalance.Request(1L, "2000000000", 3000L)
                        )))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.accountNumber").value("1234567890"))
                .andExpect(jsonPath("$.result.transactionResultType").value("S"))
                .andExpect(jsonPath("$.result.transactionId").value("transactionId"))
                .andExpect(jsonPath("$.result.amount").value(12345L));
    }

    @Test
    @DisplayName("잔액 사용 취소 성공")
    void cancel_balance_success() throws Exception {
        //given
        given(transactionService.cancelBalance(anyString(), anyString(), anyLong()))
                .willReturn(TransactionDto.builder()
                        .accountNumber("1234567890")
                        .transactionResultType(TransactionResultType.S)
                        .transactionId("transactionIdForCancel")
                        .amount(12345L)
                        .transactedAt(LocalDateTime.now())
                        .build()
                );

        //when
        //then

        mockMvc.perform(post("/transaction/cancel")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new CancelBalance.Request("transactionId",
                                        "2000000000", 3000L)
                        )))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.accountNumber").value("1234567890"))
                .andExpect(jsonPath("$.result.transactionResultType").value("S"))
                .andExpect(jsonPath("$.result.transactionId").value("transactionIdForCancel"))
                .andExpect(jsonPath("$.result.amount").value(12345L));
    }
}