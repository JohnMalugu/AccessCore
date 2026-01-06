package com.jcmlabs.AccessCore.Shared.Payload.Filtering;

import com.jcmlabs.AccessCore.Shared.Enums.NotificationStatus;
import com.jcmlabs.AccessCore.Shared.Enums.NotificationType;
import com.jcmlabs.AccessCore.Utilities.BaseFilterInput;
import lombok.Getter;

@Getter
public class NotificationFilteringInput extends BaseFilterInput {
    private NotificationStatus status;
    private NotificationType notificationType;
}
