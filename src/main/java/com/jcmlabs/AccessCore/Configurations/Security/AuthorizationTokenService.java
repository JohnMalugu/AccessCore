package com.jcmlabs.AccessCore.Configurations.Security;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import com.jcmlabs.AccessCore.Configurations.Security.Events.AuthEvent;
import com.jcmlabs.AccessCore.Configurations.Security.Events.TokenEventPublisher;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import com.jcmlabs.AccessCore.Utilities.ConfigurationUtilities.AuthTokenResponse;
import com.jcmlabs.AccessCore.Utilities.ConfigurationUtilities.TokenType;
import com.jcmlabs.AccessCore.Utilities.UtilityEntities.OpaqueTokenEntity;
import com.jcmlabs.AccessCore.Utilities.UtilityRepositories.OpaqueTokenRepository;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class AuthorizationTokenService {
    private final OpaqueTokenRepository repository;
    private final AuthorizationTokenCryptoService crypto;
    private final AuthorizationTokenConfigurationProperties properties;
    private final RedisSecurityService redisSecurityService;
    private final TokenEventPublisher eventPublisher;

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
