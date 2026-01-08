package com.jcmlabs.AccessCore.UserManagement.Payload.Filtering;

import com.jcmlabs.AccessCore.Utilities.BaseFilterDto;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserAccountFilters extends BaseFilterDto {
    private String username;
}
