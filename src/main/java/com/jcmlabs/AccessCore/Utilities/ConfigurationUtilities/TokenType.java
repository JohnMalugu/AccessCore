package com.jcmlabs.AccessCore.Utilities.ConfigurationUtilities;

public enum TokenType {

    ACCESS,
    REFRESH,
    PASSWORD_RESET;

    public boolean isAccess() {
        return this == ACCESS;
    }

    public boolean isRefresh() {
        return this == REFRESH;
    }

    public boolean isPasswordReset() {
        return this == PASSWORD_RESET;
    }
}

