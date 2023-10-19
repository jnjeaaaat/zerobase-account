package com.zerobase.account.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing
// createdAt, updatedAt 을 관리하기 위한 configuration
public class JpaAuditingConfiguration {
}
