package com.jcmlabs.AccessCore.Configurations.Security;

import java.util.HashSet;
import java.util.Set;

import com.jcmlabs.AccessCore.Configurations.Security.Redis.RedisSecurityService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import com.jcmlabs.AccessCore.Utilities.ConfigurationUtilities.AuthTokenResponse;

import lombok.RequiredArgsConstructor;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthorizationServiceHelper {

    private final AuthenticationManager authenticationManager;
    private final AuthorizationTokenService authTokenService;
    private final RedisSecurityService redisSecurity;


    public AuthTokenResponse login(String username, String password, String clientIp, String deviceId) {

        // 1️⃣ Authenticate credentials ONLY
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        Set<String> dummyScope = new HashSet<>();
        dummyScope.add("Admin");

        // 2️⃣ Decide if MFA is required
        if (mfaRequired(username, clientIp, deviceId, dummyScope)) {
            return authTokenService.issueMfaChallenge(username, clientIp, deviceId);
        }

        // 3️⃣ Issue normal tokens
        return authTokenService.issueTokens(username, clientIp, dummyScope);
    }

    public AuthTokenResponse refresh(String refreshToken, String clientIp) {
        return authTokenService.refresh(refreshToken, clientIp)
                .orElseThrow(() -> new BadCredentialsException("Invalid or expired refresh token"));
    }

    public void revokeToken(String authorizationHeader, String clientIP){
        if(authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new BadCredentialsException("Missing access token");
        }
        String signedAccessToken = authorizationHeader.substring(7);
        authTokenService.revokeToken(signedAccessToken, clientIP);
    }

    public void forgotPassword(String username, String clientIp) {
        try {
            // Rate limiting check
            if (!redisSecurity.allowForgotPassword(username, clientIp)) {
                log.warn("Rate limit exceeded for forgot password: username={}, ip={}", username, clientIp);
                return; // Silent fail - security best practice
            }

            authTokenService.issuePasswordResetToken(username, clientIp);

        } catch (Exception e) {
            // We don't expose internal errors in forgot password
            log.error("Forgot password failed silently for username={}, ip={}: {}", username, clientIp, e.getMessage());
        }
    }

    public void changePassword(String username, String currentPassword, String newPassword, String confirmPassword, String clientIp) {
        authTokenService.changePassword(username, currentPassword, newPassword, confirmPassword, clientIp);
    }


    public void resetPassword(String token, String password,String newPassword, String clientIp) {
            authTokenService.resetPassword(token, password,newPassword, clientIp);
    }

    private boolean mfaRequired(String username, String ip, String deviceId, Set<String> scopes) {
        return scopes.contains("ADMIN")
                || !redisSecurity.isTrustedDevice(username, deviceId)
                || !redisSecurity.isKnownIp(username, ip);
    }

    public AuthTokenResponse verifyMfa(
            String mfaToken,
            String code,
            String deviceId,
            String ip
    ) {
        return authTokenService.verifyMfa(mfaToken, code, deviceId, ip);
    }

}
