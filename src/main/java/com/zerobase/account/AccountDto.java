package com.zerobase.account;

import lombok.*;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;

import java.time.LocalDateTime;

@Slf4j
@Getter
@Setter
@ToString
@AllArgsConstructor
@RequiredArgsConstructor
public class AccountDto {
    private String accountNumber;
    private String nickName;
    private LocalDateTime registeredAt;

    public void log() {
        log.error("error is occurred.");
    }
}
