package com.jcmlabs.AccessCore.Utilities.SecurityContext;

import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.io.Serializable;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Builder
@Jacksonized
public final class UserPrincipal implements Serializable {

    private final Long id;
    private final String uuid;
    private final String username;
    private final AccountStatus status;
    private final Set<String> authorities;


    public boolean isActive() {
        return status == AccountStatus.ACTIVE;
    }

    public Set<GrantedAuthority> getAuthorities() {
        return authorities == null
                ? Set.of()
                : authorities.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toUnmodifiableSet());
    }
}
