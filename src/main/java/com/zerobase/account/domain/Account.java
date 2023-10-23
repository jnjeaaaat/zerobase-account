package com.zerobase.account.domain;

import com.zerobase.account.exception.AccountException;
import com.zerobase.account.type.AccountStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

import static com.zerobase.account.type.ErrorCode.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Account extends BaseEntity {
    @ManyToOne
    private AccountUser accountUser;
    private String accountNumber;

    @Enumerated(EnumType.STRING)
    private AccountStatus accountStatus;
    private Long balance;

    private LocalDateTime registeredAt;
    private LocalDateTime unRegisteredAt;

    public void useBalance(Long amount) {
        if (amount > balance) {
            throw new AccountException(AMOUNT_EXCEED_BALANCE);
        }
        balance -= amount;
    }

    public void cancelBalance(Long amount) {
        if (amount < 0) {
            throw new AccountException(INVALID_REQUEST);
        }
        balance += amount;
    }
}
