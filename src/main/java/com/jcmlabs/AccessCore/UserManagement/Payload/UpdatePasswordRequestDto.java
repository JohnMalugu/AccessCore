package com.jcmlabs.AccessCore.UserManagement.Payload;


import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record UpdatePasswordRequestDto(
        String username,
        String token,
        @Size(min = 8, message = "Password must be at least 8 characters") String password,
        String confirmPassword,
        String clientIP)
        {
        public boolean passwordsMatch() {
                return password != null && password.equals(confirmPassword);
        }
}
