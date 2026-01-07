package com.jcmlabs.AccessCore.Configurations.Security;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import com.jcmlabs.AccessCore.Configurations.Security.KafkaEvents.AuthEvent;
import com.jcmlabs.AccessCore.Configurations.Security.KafkaEvents.TokenEventPublisher;
import com.jcmlabs.AccessCore.UserManagement.Payload.UpdatePasswordRequestDto;
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
        OpaqueTokenEntity resetToken = create(username, clientIP, TokenType.PASSWORD_RESET, null);
        cacheToken(resetToken);
        String signedToken = crypto.sign(resetToken.getTokenValue(), TokenType.PASSWORD_RESET);
        eventPublisher.publish(new AuthEvent("PASSWORD_RESET_REQUESTED", username, "RESET", clientIP));
        // TODO: Send email with signedToken
        log.info("Password reset token generated for {}: {}", username, signedToken);
    }

    @Transactional
    public void resetPassword(String signedResetToken, String password, String newPassword, String clientIp) {
        OpaqueTokenEntity token = validate(signedResetToken, TokenType.PASSWORD_RESET)
                .filter(t -> t.getUserIp().equals(clientIp))
                .orElseThrow(() -> new BadCredentialsException("Invalid or expired reset token"));

        // Check if already used (Redis replay protection)
        if (!redisSecurityService.markResetTokenUsed(token.getTokenValue())) {
            throw new BadCredentialsException("Reset token already used");
        }

        UpdatePasswordRequestDto passwordRequest = UpdatePasswordRequestDto.builder()
                .username(token.getUsername())
                .password(password)
                .confirmPassword(newPassword)
                .clientIP(clientIp)
                .build();

        userAccountService.updatePassword(passwordRequest);


        // THEN: Invalidate token
        token.setActive(false);
        repository.save(token);
        redisSecurityService.invalidateToken(token.getTokenValue(), "RESET");

        // Publish event
        eventPublisher.publish(new AuthEvent(
                "PASSWORD_RESET_COMPLETED", token.getUsername(), "RESET", clientIp
        ));

        // FINALLY: Revoke all active sessions
        revokeAllUserTokens(token.getUsername());
    }

    private void revokeAllUserTokens(String username) {
        repository.findAllByUsernameAndActiveTrue(username).forEach(token -> {
            token.setActive(false);
            repository.save(token);
            redisSecurityService.invalidateToken(token.getTokenValue(), token.getTokenType().name());
        });
    }

    private void cacheToken(OpaqueTokenEntity token) {
        long ttl = Duration.between(Instant.now(), token.getExpiresAt()).getSeconds();
        redisSecurityService.cacheToken(token.getTokenValue(), token.getTokenType().name(), ttl);
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

    public void revokeToken(String signedToken, String clientIP) {
        validate(signedToken, TokenType.ACCESS).filter(t -> t.getUserIp().equals(clientIP)).ifPresent(access -> {
            access.setActive(false);
            repository.save(access);

            repository.findFirstByUsernameAndTokenTypeAndActiveTrue(access.getUsername(), TokenType.REFRESH).ifPresent(refresh -> {
                refresh.setActive(false);
                repository.save(refresh);
            });
        });
    }

    public boolean resetPassword(String signedResetToken, String newPassword) {

        return false;
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
