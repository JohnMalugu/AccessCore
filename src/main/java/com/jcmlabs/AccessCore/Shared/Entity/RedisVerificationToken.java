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
public class RedisVerificationToken implements Serializable {

    private Long userId;

    private String emailToken;

    private String phoneOtp;

    private int otpAttempts = 0;

    private Instant expiresAt;

    private boolean used = false;
}
