package com.jcmlabs.AccessCore.Shared.Payload.Request;

public record EmailDto(String recipient, String subject, String body, boolean isHtml) {}
