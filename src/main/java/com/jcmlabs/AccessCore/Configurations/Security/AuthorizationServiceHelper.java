package com.jcmlabs.AccessCore.Configurations.Security;

import java.util.Set;

import org.springframework.security.authentication.AuthenticationManager;
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

    public AuthTokenResponse login(String username, String password, String clientIp, Set<String> scopes){
        Authentication authentication = authenticate(username, password);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        return authTokenService.issueTokens(username, username, scopes);
    }

    private Authentication authenticate(String username, String password){
        return authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(username, password)
        );
    }
}
