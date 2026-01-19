package com.jcmlabs.AccessCore.Shared.Entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RedisMfaChallenge {
    private String username;
    private String ip;
    private String deviceId;
    private String otpHash;
    private int attempts;
}
