package com.jcmlabs.AccessCore.UserManagement.Services;

import com.jcmlabs.AccessCore.UserManagement.Entities.UserAccountEntity;
import com.jcmlabs.AccessCore.UserManagement.Payload.Filtering.UserAccountFilters;
import com.jcmlabs.AccessCore.UserManagement.Payload.Request.UpdatePasswordRequestDto;
import com.jcmlabs.AccessCore.Utilities.BaseResponse;

import java.time.Instant;

public interface UserAccountService {
    void updatePassword(UpdatePasswordRequestDto input);
    BaseResponse<UserAccountEntity> getAllUsers(UserAccountFilters filters);
    UserAccountEntity getActiveUserOrThrow(String username);
    void verifyPassword(String username, String rawPassword);
    Instant getPasswordChangedAt(String username);
}
