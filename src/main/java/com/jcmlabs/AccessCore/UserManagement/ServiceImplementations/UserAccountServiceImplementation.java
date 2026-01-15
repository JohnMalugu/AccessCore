package com.jcmlabs.AccessCore.UserManagement.ServiceImplementations;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.jcmlabs.AccessCore.UserManagement.Payload.Filtering.UserAccountFilters;
import com.jcmlabs.AccessCore.UserManagement.Payload.Request.UpdatePasswordRequestDto;
import com.jcmlabs.AccessCore.UserManagement.Services.UserAccountService;
import com.jcmlabs.AccessCore.Utilities.BaseResponse;
import com.jcmlabs.AccessCore.Utilities.ConfigurationUtilities.PasswordPolicy;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.jcmlabs.AccessCore.UserManagement.Entities.UserAccountEntity;
import com.jcmlabs.AccessCore.UserManagement.Repositories.UserAccountRepository;

import lombok.RequiredArgsConstructor;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserAccountServiceImplementation implements UserDetailsService, UserAccountService {

    private final UserAccountRepository userAccountRepository;
    private final PasswordPolicy passwordPolicy;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(@NonNull String username) throws UsernameNotFoundException {
        Optional<UserAccountEntity> optionalUser = userAccountRepository.findFirstByUsername(username);
        if(optionalUser.isPresent()){
            UserAccountEntity user = optionalUser.get();
            String[] authorities = user.getAuthorities() != null  && !user.getAuthorities().isEmpty() ? getAuthorities(user) : new String[0];
            return User.builder().username(user.getUsername()).password(user.getPassword()).roles(authorities).build();
        } else {
            throw new UsernameNotFoundException("User with given username " + username + " Could not be found");
        }
        
    }
    
    private String[] getAuthorities(UserAccountEntity user) {
		List<String> authorities = new ArrayList<>();
		for (GrantedAuthority authority : user.getAuthorities()) {
			authorities.add(authority.getAuthority());
		}
        return authorities.toArray(new String[0]);
	}

    @Override
    public void updatePassword(UpdatePasswordRequestDto input) {

        log.debug("Starting password update process for: {} [IP: {}]", input.username(), input.clientIP());

        if (!input.passwordsMatch()) {
            log.warn("Password mismatch attempt for user: {}", input.username());
            throw new IllegalArgumentException("New password and confirmation do not match.");
        }
        passwordPolicy.validate(input.password());

        UserAccountEntity user = userAccountRepository.findActiveByUsername(input.username())
                .orElseThrow(() -> {
                    log.error("[SECURITY ALERT] Password update targeted non-existent user: {}", input.username());
                    return new BadCredentialsException("Invalid request parameters.");
                });

        if (passwordEncoder.matches(input.password(), user.getPassword())) {
            log.warn("[AUDIT] User {} attempted to reuse current password from IP: {}", input.username(), input.clientIP());
            throw new IllegalArgumentException("New password cannot be the same as your current password.");
        }
        try {
            user.setPassword(passwordEncoder.encode(input.password()));
            user.setPasswordChangedAt(Instant.now());
            user.setLastModifiedIp(input.clientIP());
            userAccountRepository.save(user);
            log.info("✅ [SECURITY] Password successfully updated for user: {} | Source IP: {}", input.username(), input.clientIP());
        } catch (Exception e) {
            log.error("❌ Failed to persist password change for user: {}", input.username(), e);
            throw new RuntimeException("An internal error occurred while saving your new password.");
        }
    }

    @Override
    public BaseResponse<UserAccountEntity> getAllUsers(UserAccountFilters filters) {
        return null;
    }

    @Override
    public UserAccountEntity getActiveUserOrThrow(String username) {
        return userAccountRepository.findActiveByUsername(username).orElseThrow(() -> {
            log.warn("[SECURITY] Lookup for non-existent or inactive user: {}", username);
            return new UsernameNotFoundException("User not found");
        });
    }

    public void verifyPassword(String username, String rawPassword) {
        UserAccountEntity user = getActiveUserOrThrow(username);

        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new BadCredentialsException("Invalid current password");
        }
    }

    public Instant getPasswordChangedAt(String username) {
        return userAccountRepository.findByUsername(username)
                .map(UserAccountEntity::getPasswordChangedAt)
                .orElse(Instant.EPOCH);
    }

}
