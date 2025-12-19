package com.jcmlabs.AccessCore.UserManagement.Payload;

import lombok.Getter;

@Getter
public class UserAccountInput {
    private String firstName;
    private String lastName;
    private String username;
    private String password;
    private String role;
}
