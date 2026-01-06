package com.jcmlabs.AccessCore.Shared.Payload.Request;

import lombok.Builder;

@Builder
public record EmailDto(String recipient, String subject, String body, boolean isHtml) {}
