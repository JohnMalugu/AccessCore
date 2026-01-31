package com.jcmlabs.AccessCore.Shared.Entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RedisRegistrationVerification implements Serializable {

    private Long userId;

    private boolean emailVerified;
    private boolean phoneVerified;

    private String emailTokenHash;
    private String phoneOtpHash;

    private int otpAttempts;
    private Instant expiresAt;
}

