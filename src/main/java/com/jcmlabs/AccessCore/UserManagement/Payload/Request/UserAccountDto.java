package com.jcmlabs.AccessCore.UserManagement.Payload.Request;

import lombok.Getter;

@Getter
public class UserAccountDto {
    private String firstName;
    private String lastName;
    private String username;
    private String password;
    private String role;
}
