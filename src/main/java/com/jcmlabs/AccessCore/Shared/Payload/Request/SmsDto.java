package com.jcmlabs.AccessCore.Shared.Payload.Request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

import java.time.Instant;

@Builder
public record SmsDto(
        @NotBlank String receiver,
        @NotBlank String message,
        Instant date
){}
