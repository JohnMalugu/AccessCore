package com.jcmlabs.AccessCore.Shared.Payload.Filtering;

import com.jcmlabs.AccessCore.Shared.Enums.NotificationStatus;
import com.jcmlabs.AccessCore.Shared.Enums.NotificationType;
import com.jcmlabs.AccessCore.Utilities.BaseFilterDto;
import lombok.Getter;

@Getter
public class NotificationFilteringDto extends BaseFilterDto {
    private NotificationStatus status;
    private NotificationType notificationType;
}
