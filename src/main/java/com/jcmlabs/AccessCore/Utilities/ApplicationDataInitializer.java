package com.jcmlabs.AccessCore.Utilities;

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
    public void run(ApplicationArguments args) {

        seedDefaultUser();
    }

    private void seedDefaultUser() {

        if (userRepository.existsByUsername("admin")) {
            log.info("Default admin user already exists — skipping");
            return;
        }

        log.info("Creating default admin user");

        UserAccountEntity user = new UserAccountEntity();
        user.setFirstName("System");
        user.setLastName("Administrator");
        user.setUsername("admin");
        user.setPassword(passwordEncoder.encode("@dmin@!23"));

        userRepository.save(user);

        log.info("Default admin user created");
    }
}