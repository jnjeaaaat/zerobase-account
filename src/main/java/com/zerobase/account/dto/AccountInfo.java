package com.zerobase.account.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountInfo {
    private String accountNumber;
    private Long balance;
}
