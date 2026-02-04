package com.jcmlabs.AccessCore.Utilities.SecurityContext;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtil {
    private void SecurityUtils() {}

    public static UserPrincipal currentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()) {
            throw new BadCredentialsException("Unauthenticated");
        }

        Object principal = auth.getPrincipal();

        if ("anonymousUser".equals(principal)) {
            throw new BadCredentialsException("Anonymous user is not allowed");
        }

        if (!(principal instanceof UserPrincipal userPrincipal)) {
            String principalType = (principal == null) ? "null" : principal.getClass().getSimpleName();
            throw new BadCredentialsException("Unsupported principal type: " + principalType);
        }

        return userPrincipal;
    }
}
