package com.jcmlabs.AccessCore.Configurations.Security;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import com.jcmlabs.AccessCore.Configurations.RabbitMQ.RabbitMQProducer;
import com.jcmlabs.AccessCore.Configurations.Security.KafkaEvents.AuthEvent;
import com.jcmlabs.AccessCore.Configurations.Security.KafkaEvents.TokenEventPublisher;
import com.jcmlabs.AccessCore.Configurations.Security.Redis.RedisSecurityService;
import com.jcmlabs.AccessCore.Shared.Entity.RedisAccessSession;
import com.jcmlabs.AccessCore.Shared.Enums.EmailType;
import com.jcmlabs.AccessCore.Shared.Enums.NotificationStatus;
import com.jcmlabs.AccessCore.Shared.Enums.NotificationType;
import com.jcmlabs.AccessCore.Shared.Payload.Request.NotificationDto;
import com.jcmlabs.AccessCore.UserManagement.Entities.UserAccountEntity;
import com.jcmlabs.AccessCore.UserManagement.Payload.Request.UpdatePasswordRequestDto;
import com.jcmlabs.AccessCore.UserManagement.Services.UserAccountService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

import com.jcmlabs.AccessCore.Utilities.ConfigurationUtilities.AuthTokenResponse;
import com.jcmlabs.AccessCore.Utilities.ConfigurationUtilities.TokenType;
import com.jcmlabs.AccessCore.Utilities.UtilityEntities.OpaqueTokenEntity;
import com.jcmlabs.AccessCore.Utilities.UtilityRepositories.OpaqueTokenRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthorizationTokenService {
    private final OpaqueTokenRepository repository;
    private final AuthorizationTokenCryptoService crypto;
    private final AuthorizationTokenConfigurationProperties properties;
    private final RedisSecurityService redisSecurityService;
    private final TokenEventPublisher eventPublisher;
    private final UserAccountService userAccountService;
    private final RabbitMQProducer rabbitmqProducer;

    public AuthTokenResponse issueTokens(String username, String clientIP, Set<String> scopes) {
        OpaqueTokenEntity access = create(username, clientIP, TokenType.ACCESS, scopes);
        OpaqueTokenEntity refresh = create(username, clientIP, TokenType.REFRESH, null);

        cacheToken(access);
        cacheToken(refresh);

        String signedAccess = crypto.sign(access.getTokenValue(), TokenType.ACCESS);
        String signedRefresh = crypto.sign(refresh.getTokenValue(), TokenType.REFRESH);

        eventPublisher.publish(new AuthEvent("LOGIN", username, "ACCESS", clientIP, Instant.now()));

        return new AuthTokenResponse(signedAccess, signedRefresh, "Bearer", seconds(access), seconds(refresh), Instant.now(), scopes);
    }

    public void issuePasswordResetToken(String username, String clientIP) {

        UserAccountEntity user = userAccountService.getActiveUserOrThrow(username);

        // ✅ Invalidate previous reset tokens (important security hardening)
        repository.findAllByUsernameAndTokenTypeAndActiveTrue(username, TokenType.PASSWORD_RESET)
                .forEach(t -> {
                    t.setActive(false);
                    repository.save(t);
                    redisSecurityService.invalidateTokenFlag(t.getTokenValue(), "RESET");
                });

        OpaqueTokenEntity resetToken = create(username, clientIP, TokenType.PASSWORD_RESET, null);
        cacheToken(resetToken);

        String signedToken = crypto.sign(resetToken.getTokenValue(), TokenType.PASSWORD_RESET);

        eventPublisher.publish(
                new AuthEvent("PASSWORD_RESET_REQUESTED", username, "RESET", clientIP)
        );

        String resetUrl = "https://accesscore/reset-password?token=" + signedToken;

        NotificationDto notification = NotificationDto.builder()
                .emailTo(username)
                .fullName(user.getFirstName() + " " + user.getLastName())
                .message("""
                A password reset was requested for your account.

                Reset your password using the link below:
                %s

                If you did not request this, please ignore this message.
                """.formatted(resetUrl))
                .notificationType(NotificationType.EMAIL)
                .status(NotificationStatus.PENDING)
                .emailType(EmailType.RESET_PASSWORD)
                .build();

        rabbitmqProducer.sendMessageToRabbitMQ(notification);

        log.info("🔐 Password reset token issued for user={}", username);
    }


    @Transactional
    public void resetPassword(String signedResetToken, String password, String newPassword, String clientIp) {

        OpaqueTokenEntity token = validate(signedResetToken, TokenType.PASSWORD_RESET)
                .filter(t -> t.getUserIp().equals(clientIp)).orElseThrow(() -> new BadCredentialsException("Invalid or expired reset token"));

        // ✅ Replay protection
        if (!redisSecurityService.markResetTokenUsed(token.getTokenValue())) {
            throw new BadCredentialsException("Reset token already used");
        }

        UpdatePasswordRequestDto request = UpdatePasswordRequestDto.builder()
                .username(token.getUsername())
                .password(password)
                .confirmPassword(newPassword)
                .clientIP(clientIp)
                .build();

        userAccountService.updatePassword(request);

        // ✅ Invalidate token
        token.setActive(false);
        repository.save(token);
        redisSecurityService.invalidateTokenFlag(token.getTokenValue(), "RESET");

        eventPublisher.publish(new AuthEvent("PASSWORD_RESET_COMPLETED", token.getUsername(), "RESET", clientIp));

        // ✅ Force logout everywhere
        revokeAllUserTokens(token.getUsername());
    }


    private void revokeAllUserTokens(String username) {

        Set<String> tokenIds = redisSecurityService.getUserSessions(username);
        for (String tokenId : tokenIds) {
            redisSecurityService.invalidateAccessSession(tokenId);
        }
        redisSecurityService.clearUserSessions(username);
        repository.findAllByUsernameAndActiveTrue(username).forEach(token -> {
            token.setActive(false);
            repository.save(token);
        });
    }

    private void cacheToken(OpaqueTokenEntity token) {
        long ttl = Duration.between(Instant.now(), token.getExpiresAt()).getSeconds();

        RedisAccessSession session = new RedisAccessSession(
                token.getTokenValue(),
                token.getUsername(),
                token.getUserIp(),
                token.getTokenType()
        );

        redisSecurityService.storeAccessSession(session, ttl);
        redisSecurityService.addUserSession(token.getUsername(), token.getTokenValue(), ttl);

    }


    public void revokeToken(String signedToken, String clientIP) {
        validate(signedToken, TokenType.ACCESS)
                .filter(t -> t.getUserIp().equals(clientIP))
                .ifPresent(access -> {
                    access.setActive(false);
                    repository.save(access);

                    redisSecurityService.invalidateAccessSession(access.getTokenValue());

                    repository.findFirstByUsernameAndTokenTypeAndActiveTrue(
                            access.getUsername(), TokenType.REFRESH
                    ).ifPresent(refresh -> {
                        refresh.setActive(false);
                        repository.save(refresh);
                        redisSecurityService.invalidateAccessSession(refresh.getTokenValue());
                    });
                });
    }


    public Optional<AuthTokenResponse> refresh(String signedRefreshToken, String clientIp) {
        return validate(signedRefreshToken, TokenType.REFRESH).filter(t -> t.getUserIp().equals(clientIp)).map(old -> {
            old.setActive(false);
            repository.save(old);
            return issueTokens(old.getUsername(), clientIp, parseScopes(old.getScopes()));
        });
    }


    public Optional<OpaqueTokenEntity> validate(String signedToken, TokenType type) {
        return crypto.verify(signedToken, type).flatMap(repository::findFirstByTokenValueAndActiveTrue).filter(t -> t.getTokenType() == type).filter(t -> t.getExpiresAt().isAfter(Instant.now()));
    }

    private OpaqueTokenEntity create(String username, String ip, TokenType type, Set<String> scopes) {
        if (type.isRefresh()) {
            repository.findFirstByUsernameAndTokenTypeAndActiveTrue(username, TokenType.REFRESH).ifPresent(t -> {
                t.setActive(false);
                repository.save(t);
            });
        }

        Instant now = Instant.now();
        Instant expires = now.plus(properties.getLifespan().resolve(type));

        return repository.save(new OpaqueTokenEntity(UUID.randomUUID().toString(), username, ip, type, scopes == null ? null : String.join(" ", scopes), now, expires, true));
    }

    @Transactional
    public void changePassword(String username, String currentPassword, String newPassword, String confirmPassword, String clientIp) {
        if (!redisSecurityService.allowChangePassword(username, clientIp)) {
            throw new BadCredentialsException("Too many attempts");
        }
        userAccountService.verifyPassword(username, currentPassword);
        if (!newPassword.equals(confirmPassword)) {
            throw new IllegalArgumentException("Passwords do not match");
        }
        userAccountService.updatePassword(UpdatePasswordRequestDto.builder().username(username).password(currentPassword).confirmPassword(newPassword).clientIP(clientIp).build());
        revokeAllUserTokens(username);
        eventPublisher.publish(new AuthEvent("PASSWORD_CHANGED", username, "ACCESS", clientIp, Instant.now()));
    }




    /*
    HELPER METHODS

    * */

    private long seconds(OpaqueTokenEntity t) {
        return t.getExpiresAt().getEpochSecond() - t.getIssuedAt().getEpochSecond();
    }

    private Set<String> parseScopes(String scopes) {
        return scopes == null || scopes.isBlank() ? Set.of() : Set.of(scopes.split(" "));
    }




}
