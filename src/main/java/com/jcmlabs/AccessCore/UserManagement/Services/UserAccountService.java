package com.jcmlabs.AccessCore.UserManagement.Services;

import com.jcmlabs.AccessCore.UserManagement.Payload.UpdatePasswordRequestDto;
import com.jcmlabs.AccessCore.Utilities.BaseResponse;

public interface UserAccountService {
    void updatePassword(UpdatePasswordRequestDto input);
}
