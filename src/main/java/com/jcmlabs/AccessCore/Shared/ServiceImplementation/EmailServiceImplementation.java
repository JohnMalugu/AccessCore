package com.jcmlabs.AccessCore.Shared.ServiceImplementation;

import com.jcmlabs.AccessCore.Shared.Payload.Request.EmailInput;
import com.jcmlabs.AccessCore.Shared.Service.EmailService;
import com.jcmlabs.AccessCore.Utilities.BaseResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EmailServiceImplementation implements EmailService {
    @Override
    public BaseResponse<Void> sendEmail(EmailInput input) {
        return null;
    }
}
