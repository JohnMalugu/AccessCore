package com.jcmlabs.AccessCore.Configurations.Security;

import com.jcmlabs.AccessCore.Configurations.Security.Redis.RedisSecurityService;
import com.jcmlabs.AccessCore.Shared.Entity.RedisAccessSession;
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

    private final RedisSecurityService redisSecurityService;
    private final AuthorizationTokenCryptoService crypto;

    @Override
    protected void doFilterInternal(HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {

        String header = request.getHeader("Authorization");

        if (header == null || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String signedToken = header.substring(7);
        String clientIp = RequestClientIpUtility.getClientIpAddress(request);

        String tokenId = crypto.verify(signedToken, TokenType.ACCESS).orElse(null);
        if (tokenId == null) {
            filterChain.doFilter(request, response);
            return;
        }

        RedisAccessSession session = redisSecurityService.getSession(tokenId, TokenType.ACCESS);

        if (session == null || !session.getIp().equals(clientIp)) {
            SecurityContextHolder.clearContext();
            filterChain.doFilter(request, response);
            return;
        }

        Authentication auth = new UsernamePasswordAuthenticationToken(session.getUsername(), null, List.of());

        SecurityContextHolder.getContext().setAuthentication(auth);
        filterChain.doFilter(request, response);
    }
}


