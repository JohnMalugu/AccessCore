package com.jcmlabs.AccessCore.Configurations.Security;

import java.util.Set;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.jcmlabs.AccessCore.Utilities.ConfigurationUtilities.AuthTokenResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthorizationServiceHelper {

    private final AuthenticationManager authenticationManager;
    private final AuthorizationTokenService authTokenService;

    public AuthTokenResponse login(
            String username,
            String password,
            String clientIp,
            Set<String> scopes
    ) {
        Authentication authentication = authenticate(username, password);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return authTokenService.issueTokens(username, clientIp, scopes);
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

    private Authentication authenticate(String username, String password) {
        return authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(username, password)
        );
    }
}
