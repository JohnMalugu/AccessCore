package com.jcmlabs.AccessCore.Configurations.Security.KafkaEvents;

import java.time.Instant;

public record AuthEvent(
        String eventType,
        String username,
        String tokenType,
        String clientIp,
        Instant timestamp
) {
    public AuthEvent(String eventType, String username, String tokenType, String clientIp) {
        this(eventType, username, tokenType, clientIp, Instant.now());
    }
}
