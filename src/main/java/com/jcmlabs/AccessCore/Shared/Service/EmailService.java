package com.jcmlabs.AccessCore.Shared.Service;

import com.jcmlabs.AccessCore.Shared.Payload.Request.EmailInput;
import com.jcmlabs.AccessCore.Utilities.BaseResponse;

public interface EmailService {
    BaseResponse<Void> sendEmail(EmailInput input);
}
