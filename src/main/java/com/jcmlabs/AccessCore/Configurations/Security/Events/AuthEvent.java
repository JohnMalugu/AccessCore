package com.jcmlabs.AccessCore.Configurations.Security.Events;

import java.time.Instant;

public record AuthEvent(
        String eventType,
        String username,
        String tokenType,
        String clientIp,
        Instant timestamp
) {}
