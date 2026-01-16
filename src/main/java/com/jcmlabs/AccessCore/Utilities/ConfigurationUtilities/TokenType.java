package com.jcmlabs.AccessCore.Utilities.ConfigurationUtilities;

public enum TokenType {

    ACCESS,
    REFRESH,
    PASSWORD_RESET,
    MFA_CHALLENGE;

    public boolean isRefresh() {
        return this == REFRESH;
    }

}

