package com.jcmlabs.AccessCore.Shared.Exceptions.Domain;



import com.jcmlabs.AccessCore.Exceptions.Domain.DomainException;
import com.jcmlabs.AccessCore.Utilities.ResponseCode;
import lombok.Getter;

@Getter
public class MfaException extends DomainException {

    private final Integer code;

    private MfaException(Integer code, String message) {
        super(message);
        this.code = code;
    }

    public static MfaException invalidCode() {
        return new MfaException(
                ResponseCode.MFA_INVALID,
                "Invalid MFA code"
        );
    }

    public static MfaException expired() {
        return new MfaException(
                ResponseCode.MFA_EXPIRED,
                "MFA challenge expired"
        );
    }

    public static MfaException tooManyAttempts() {
        return new MfaException(
                ResponseCode.MFA_LOCKED,
                "Too many MFA attempts"
        );
    }

    public static MfaException mismatch() {
        return new MfaException(
                ResponseCode.MFA_MISMATCH,
                "MFA verification failed"
        );
    }
}

