package com.jcmlabs.AccessCore.Utilities;

public class ResponseCode {
    public static final int SUCCESS = 2000;
    public static final int FAILURE = 5000;
    public static final int NOT_FOUND = 4004;
    public static final int EXPIRED = 4004;
    public static final int NO_RECORD_FOUND = 4004;
    public static final int INVALID_ARGUMENT = 4001;
    public static final int INVALID_TOKEN = 4001;
    public static final int INVALID_STATE = 4001;
    public static final int UNAUTHORIZED = 4003;
    public static final int ACCESS_DENIED = 4003;
    public static final int DUPLICATE = 4009;
    public static final int TOO_MANY_REQUESTS = 4009;
    public static final int DATA_IN_USE = 4010;
    public static final int BAD_REQUEST = 4000;
    public static final int ERROR = 4000;
    public static final int METHOD_NOT_ALLOWED = 4050;
    public static final int NULL_ARGUMENT = 4002;
    public static final int EXCEPTION = 5010;
    public static final int INTERNAL_ERROR = 5001;
    public static final int DELETED = 4100;
    public static final int EXISTS = 4090;
    public static final int PENDING_VERIFICATION = 4090;
    public static final int REQUIRED_FIELD = 4220;
    public static final int PASSWORD_MISMATCH = 4221;
    public static final int LOCKED = 4222;
    public static final int VALIDATION_ERROR = 4223;

    public static final int MFA_REQUIRED = 4300;
    public static final int MFA_INVALID = 4301;
    public static final int MFA_EXPIRED = 4302;
    public static final int MFA_LOCKED = 4303;
    public static final int MFA_MISMATCH = 4304;
    public static final int MFA_NOT_CONFIGURED = 4305;
    public static final int MFA_ALREADY_VERIFIED = 4306;
}
