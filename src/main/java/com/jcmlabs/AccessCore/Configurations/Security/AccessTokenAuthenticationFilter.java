package com.jcmlabs.AccessCore.Configurations.Security;

import com.jcmlabs.AccessCore.Utilities.ConfigurationUtilities.TokenType;
import com.jcmlabs.AccessCore.Utilities.RequestClientIpUtility;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class AccessTokenAuthenticationFilter extends OncePerRequestFilter {

    private final AuthorizationTokenService tokenService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {

        String header = request.getHeader("Authorization");

        if (header == null || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String signedToken = header.substring(7);
        String clientIp = RequestClientIpUtility.getClientIpAddress(request);

        tokenService.validate(signedToken, TokenType.ACCESS).filter(t -> t.getUserIp().equals(clientIp)).ifPresent(token -> {
            Authentication auth = new UsernamePasswordAuthenticationToken(token.getUsername(), null, List.of());
            SecurityContextHolder.getContext().setAuthentication(auth);
        });

        filterChain.doFilter(request, response);
    }
}

