package com.jcmlabs.AccessCore.Shared.Payload.Request;

import jakarta.validation.constraints.NotBlank;

public record SmsDto(
        @NotBlank String receiver,
        @NotBlank String message
){}
