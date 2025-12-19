package com.jcmlabs.AccessCore.Utilities.UtilityEntities;


import java.time.Instant;

import com.jcmlabs.AccessCore.Utilities.ConfigurationUtilities.TokenType;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "opaque_tokens",
       indexes = {
           @Index(name = "idx_token_value", columnList = "tokenValue"),
           @Index(name = "idx_active_token", columnList = "username, tokenType, active")
       })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OpaqueTokenEntity {

    @Id
    @Column(nullable = false, updatable = false)
    private String tokenValue;

    @Column(nullable = false)
    private String username;

    private String userIp;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TokenType tokenType;

    private String scopes;

    @Column(nullable = false)
    private Instant issuedAt;

    @Column(nullable = false)
    private Instant expiresAt;

    @Column(nullable = false)
    private boolean active = true;
}
