package com.jcmlabs.AccessCore.Shared.Service;

import com.jcmlabs.AccessCore.Shared.Entity.NotificationEntity;
import com.jcmlabs.AccessCore.Shared.Payload.Filtering.NotificationFilteringDto;
import com.jcmlabs.AccessCore.Shared.Payload.Request.NotificationDto;
import com.jcmlabs.AccessCore.Utilities.BaseResponse;

public interface NotificationService {
    BaseResponse<NotificationEntity> filterNotifications(NotificationFilteringDto filtering);
    BaseResponse<NotificationEntity> createNotification(NotificationDto input);
    BaseResponse<NotificationEntity> updateNotificationStatus(NotificationDto input);
}
