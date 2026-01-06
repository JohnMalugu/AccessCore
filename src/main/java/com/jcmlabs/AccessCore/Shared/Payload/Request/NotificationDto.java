package com.jcmlabs.AccessCore.Shared.Payload.Request;

import com.jcmlabs.AccessCore.Shared.Enums.EmailType;
import com.jcmlabs.AccessCore.Shared.Enums.NotificationStatus;
import com.jcmlabs.AccessCore.Shared.Enums.NotificationType;

import java.time.LocalDateTime;
import java.util.List;

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
        LocalDateTime createdAt,
        EmailType emailType
        ) {}
