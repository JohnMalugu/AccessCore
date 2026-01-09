package com.jcmlabs.AccessCore.Configurations.Http.Email;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.HealthIndicator;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import jakarta.mail.Transport;
import jakarta.mail.internet.MimeMessage;

@Slf4j
@Component
@RequiredArgsConstructor
public class SmtpHealthIndicator implements HealthIndicator {

    private final JavaMailSender mailSender;

    @Override
    public Health health() {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            Transport transport = message.getSession().getTransport("smtp");
            transport.connect();
            transport.close();

            log.info("✅ SMTP health check passed");
            return Health.up().withDetail("smtp", "Available").build();

        } catch (Exception e) {
            log.error("❌ SMTP health check failed", e);
            return Health.down(e).withDetail("smtp", "Unavailable").build();
        }
    }
}

