package com.jcmlabs.AccessCore.Shared.Payload.Request;

import com.jcmlabs.AccessCore.Shared.Enums.EmailType;
import com.jcmlabs.AccessCore.Shared.Enums.NotificationStatus;
import com.jcmlabs.AccessCore.Shared.Enums.NotificationType;
import lombok.Builder;

import java.time.Instant;
import java.util.List;

/**
 * Data Transfer Object for Notifications.
 * <p>
 * DESIGN DECISIONS:
 * 1. Immutability: Uses Java Record to ensure thread-safety and data integrity
 * across the RabbitMQ pipeline.
 * <p>
 * 2. Builder Pattern: Enabled via Lombok @Builder to handle the large number
 * of optional fields (fcmTokens, emailTo, etc.) without constructor hell.
 * <p>
 * 3. toBuilder = true (The "Immutable Update" Pattern):
 * Java Records are immutable; their fields cannot be changed after creation (no setters).
 * <p>
 * In our Producer flow, the DTO initially arrives from the Controller with a null UUID.
 * Once the record is saved to the database, we receive a generated UUID and Timestamp.
 * <p>
 * Because we cannot do 'input.setUuid(id)', we use toBuilder() to:
 * a) Capture all existing data from the original request (fullName, message, etc.)
 * b) "Open" the record back into a builder state.
 * c) Override ONLY the uuid and createdAt fields with the database-confirmed values.
 * d) Build a final, complete "Payload" DTO to send to RabbitMQ.
 * <p>
 * This ensures the Consumer receives a message that perfectly matches the database record.
 *
 * <p>
 * 4. Instant vs LocalDateTime: Uses Instant for 'createdAt' to ensure
 * timezone-agnostic timestamps (UTC) across different server environments.
 */
@Builder(toBuilder = true)
public record NotificationDto(
        String uuid,
        Long userId,
        List<String> fcmTokens,
        String subject,
        String emailTo,
        String phoneTo,
        String fullName,
        String message,
        NotificationStatus status,
        NotificationType notificationType,
        String remarks,
        Instant createdAt,
        EmailType emailType
) {
}
