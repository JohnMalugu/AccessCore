package com.jcmlabs.AccessCore.Shared.ServiceImplementation;

import com.jcmlabs.AccessCore.Shared.Entity.NotificationEntity;
import com.jcmlabs.AccessCore.Shared.Payload.Filtering.NotificationFilteringInput;
import com.jcmlabs.AccessCore.Shared.Payload.Request.NotificationDto;
import com.jcmlabs.AccessCore.Shared.Service.NotificationService;
import com.jcmlabs.AccessCore.Utilities.BaseResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImplementation implements NotificationService {
    @Override
    public BaseResponse<NotificationEntity> filterNotifications(NotificationFilteringInput filtering) {
        return null;
    }

    @Override
    public BaseResponse<NotificationEntity> createNotification(NotificationDto input) {
        return null;
    }

    @Override
    public BaseResponse<NotificationEntity> updateNotificationStatus(NotificationDto input) {
        return null;
    }
}
