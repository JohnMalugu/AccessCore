package com.jcmlabs.AccessCore.Utilities;

import org.jspecify.annotations.NonNull;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.jcmlabs.AccessCore.UserManagement.Entities.UserAccountEntity;
import com.jcmlabs.AccessCore.UserManagement.Repositories.UserAccountRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class ApplicationDataInitializer implements ApplicationRunner {

    private final UserAccountRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(@NonNull ApplicationArguments args) {
        saveUserIfNotFound(
                "admin",
                "@dmin@!23",
                "System",
                "Administrator"
        );

        saveUserIfNotFound(
                "johnmalugu99@gmail.com",
                "M@nager123",
                "John",
                "Malugu"
        );
    }

    private void saveUserIfNotFound(String username, String rawPassword, String firstName, String lastName) {
        if (userRepository.existsByUsername(username)) {
            log.info("User '{}' already exists — skipping", username);
            return;
        }

        log.info("Creating user: {}", username);

        UserAccountEntity user = new UserAccountEntity();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(rawPassword));

        userRepository.save(user);
        log.info("User '{}' created successfully", username);
    }
}