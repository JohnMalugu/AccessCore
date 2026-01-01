package com.jcmlabs.AccessCore.UserManagement.Payload;


import jakarta.validation.constraints.Size;

public record PasswordUpdateRequestInput(

        String username,

        String token,

        @Size(min = 6, message = "Password must be at least 6 characters")
        String password
) {
}
