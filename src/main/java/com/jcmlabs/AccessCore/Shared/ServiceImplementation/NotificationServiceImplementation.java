package com.jcmlabs.AccessCore.Shared.ServiceImplementation;

import com.jcmlabs.AccessCore.Shared.Entity.NotificationEntity;
import com.jcmlabs.AccessCore.Shared.Enums.NotificationStatus;
import com.jcmlabs.AccessCore.Shared.Payload.Filtering.NotificationFilteringInput;
import com.jcmlabs.AccessCore.Shared.Payload.Request.NotificationDto;
import com.jcmlabs.AccessCore.Shared.Repository.NotificationRepository;
import com.jcmlabs.AccessCore.Shared.Service.NotificationService;
import com.jcmlabs.AccessCore.Utilities.BaseResponse;
import com.jcmlabs.AccessCore.Utilities.PaginationUtilities.PageableConfigurations;
import com.jcmlabs.AccessCore.Utilities.ResponseCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImplementation implements NotificationService {

    private final PageableConfigurations pageableConfigurations;
    private final NotificationRepository notificationRepository;
    @Override
    public BaseResponse<NotificationEntity> filterNotifications(NotificationFilteringInput filtering) {
        return null;
    }

    @Override
    public BaseResponse<NotificationEntity> createNotification(NotificationDto input) {
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
            return new BaseResponse<>(true, ResponseCode.SUCCESS, "Notification created successfully", saved);

        } catch (Exception e) {
            log.error("Failed to create notification entity", e);
            return new BaseResponse<>(false, ResponseCode.EXCEPTION, "Error saving notification");
        }
    }

    @Override
    public BaseResponse<NotificationEntity> updateNotificationStatus(NotificationDto input) {
        return null;
    }
}
