package com.jcmlabs.AccessCore.UserManagement.Payload.Request;

public record MfaVerifyRequestDto (
        String mfaToken,
        String code,
        String deviceId
){ }
