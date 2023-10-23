package com.zerobase.account.domain;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class AccountUser extends BaseEntity {
    private String name;
}
