package com.jcmlabs.AccessCore.Shared.Service;

import com.jcmlabs.AccessCore.Shared.Payload.Request.SmsDto;
import com.jcmlabs.AccessCore.Utilities.BaseResponse;

public interface SmsService {
    BaseResponse<Void> sendSms(SmsDto input);
}
