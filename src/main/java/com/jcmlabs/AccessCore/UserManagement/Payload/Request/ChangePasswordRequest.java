package com.jcmlabs.AccessCore.UserManagement.Payload.Request;

public record ChangePasswordRequest(
        String currentPassword,
        String newPassword,
        String confirmPassword
) {}

