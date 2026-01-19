package com.jcmlabs.AccessCore.Utilities.ConfigurationUtilities;

import java.time.Instant;
import java.util.Set;

public record AuthTokenResponse(
        String accessToken,
        String refreshToken,
        String tokenType,
        Long accessTokenExpiresIn,
        Long refreshTokenExpiresIn,
        Instant issuedAt,
        Set<String> scopes,
        boolean mfaRequired
) {
    public static AuthTokenResponse mfaRequired(String mfaToken) {
        return new AuthTokenResponse(mfaToken, null, "MFA", null, null, Instant.now(), Set.of(), true);
    }
}

