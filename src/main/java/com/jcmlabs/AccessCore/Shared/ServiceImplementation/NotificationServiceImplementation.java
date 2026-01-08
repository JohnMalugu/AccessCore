package com.jcmlabs.AccessCore.Shared.ServiceImplementation;

import com.jcmlabs.AccessCore.Shared.Entity.NotificationEntity;
import com.jcmlabs.AccessCore.Shared.Enums.NotificationStatus;
import com.jcmlabs.AccessCore.Shared.Payload.Filtering.NotificationFilteringDto;
import com.jcmlabs.AccessCore.Shared.Payload.Request.NotificationDto;
import com.jcmlabs.AccessCore.Shared.Repository.NotificationRepository;
import com.jcmlabs.AccessCore.Shared.Service.NotificationService;
import com.jcmlabs.AccessCore.Utilities.BaseResponse;
import com.jcmlabs.AccessCore.Utilities.PaginationUtilities.PageableConfigurations;
import com.jcmlabs.AccessCore.Utilities.ResponseCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImplementation implements NotificationService {

    private final PageableConfigurations pageableConfigurations;
    private final NotificationRepository notificationRepository;

    @Override
    public BaseResponse<NotificationEntity> filterNotifications(NotificationFilteringDto filtering) {
        log.info("🔍 [FILTER] Fetching notifications with criteria: {}", filtering);
        // Implementation here...
        return null;
    }

    @Override
    public BaseResponse<NotificationEntity> createNotification(NotificationDto input) {
        log.info("🆕 [CREATE] Attempting to create notification for: {}", input.fullName());
        try {
            NotificationEntity notification = NotificationEntity.builder()
                    .subject(input.subject())
                    .emailTo(input.emailTo())
                    .phoneTo(input.phoneTo())
                    .fullName(input.fullName())
                    .message(input.message())
                    .notificationType(input.notificationType())
                    .status(NotificationStatus.PENDING)
                    .remarks(input.remarks())
                    .build();

            NotificationEntity saved = notificationRepository.save(notification);

            log.info("✅ [CREATE] Notification saved successfully with ID: {}", saved.getUuid());
            return new BaseResponse<>(true, ResponseCode.SUCCESS, "Notification created successfully", saved);

        } catch (Exception e) {
            log.error("❌ [CREATE] Failed to create notification entity. Input: {}", input, e);
            return new BaseResponse<>(false, ResponseCode.EXCEPTION, "Error saving notification");
        }
    }

    @Override
    public BaseResponse<NotificationEntity> updateNotificationStatus(NotificationDto input) {
        log.info("🔄 [UPDATE] Request to update status to {} for UUID: {}", input.status(), input.uuid());
        try {
            Optional<NotificationEntity> existing = notificationRepository.findFirstByUuid(input.uuid());

            if (existing.isEmpty()) {
                log.warn("⚠️ [UPDATE] Failed: No notification found for UUID: {}", input.uuid());
                return new BaseResponse<>(false, ResponseCode.NO_RECORD_FOUND, "Notification not found");
            }

            NotificationEntity notification = existing.get();
            NotificationStatus oldStatus = notification.getStatus();
            notification.setStatus(input.status());

            NotificationEntity updated = notificationRepository.save(notification);

            log.info("✅ [UPDATE] Status changed from {} to {} for UUID: {}", oldStatus, updated.getStatus(), updated.getUuid());
            return new BaseResponse<>(true, ResponseCode.SUCCESS, "Status updated successfully", updated);

        } catch (Exception e) {
            log.error("❌ [UPDATE] Failed to update status for UUID: {}. Target Status: {}", input.uuid(), input.status(), e);
            return new BaseResponse<>(false, ResponseCode.EXCEPTION, "Internal error updating status");
        }
    }
}