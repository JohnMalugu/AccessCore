package com.jcmlabs.AccessCore.UserManagement.Payload.Request;


import java.util.Set;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequestDto(

    @NotBlank(message = "Username is required")
    String username,

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    String password,

    Set<String> scopes

) {}
