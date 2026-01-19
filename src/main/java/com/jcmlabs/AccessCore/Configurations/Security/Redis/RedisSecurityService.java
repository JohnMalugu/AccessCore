package com.jcmlabs.AccessCore.Configurations.Security.Redis;

import com.jcmlabs.AccessCore.Shared.Entity.RedisAccessSession;
import com.jcmlabs.AccessCore.Shared.Entity.RedisMfaChallenge;
import com.jcmlabs.AccessCore.Utilities.ConfigurationUtilities.TokenType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisSecurityService {

    private final StringRedisTemplate stringRedis;
    private final RedisTemplate<String, RedisAccessSession> sessionRedis;
    private final RedisTemplate<String, RedisMfaChallenge> mfaRedis;

    /* ================= RATE LIMITING ================= */

    public boolean allowForgotPassword(String username, String ip) {
        String key = "auth:rate:forgot:" + username + ":" + ip;
        Long count = stringRedis.opsForValue().increment(key);

        if (count != null && count == 1) {
            stringRedis.expire(key, Duration.ofMinutes(15));
        }
        return count != null && count <= 5;
    }

    public boolean allowChangePassword(String username, String ip) {
        String key = "auth:rate:change:" + username + ":" + ip;
        Long count = stringRedis.opsForValue().increment(key);

        if (count != null && count == 1) {
            stringRedis.expire(key, Duration.ofMinutes(10));
        }
        return count != null && count <= 3;
    }

    /* ================= RESET TOKEN ================= */

    public boolean markResetTokenUsed(String tokenId) {
        return Boolean.TRUE.equals(
                stringRedis.opsForValue()
                        .setIfAbsent("auth:reset:used:" + tokenId, "1", Duration.ofMinutes(30))
        );
    }

    /* ================= USER SESSION TRACKING ================= */

    public void addUserSession(String username, String tokenId, long ttlSeconds) {
        String key = "user:sessions:" + username;
        stringRedis.opsForSet().add(key, tokenId);
        stringRedis.expire(key, ttlSeconds, TimeUnit.SECONDS);
    }

    public Set<String> getUserSessions(String username) {
        return stringRedis.opsForSet().members("user:sessions:" + username);
    }

    public void clearUserSessions(String username) {
        stringRedis.delete("user:sessions:" + username);
    }

    /* ================= ACCESS / REFRESH SESSION ================= */

    public void storeSession(RedisAccessSession session, long ttlSeconds) {
        sessionRedis.opsForValue().set(
                prefix(session.getTokenType()) + session.getTokenId(),
                session,
                ttlSeconds,
                TimeUnit.SECONDS
        );
    }

    public RedisAccessSession getSession(String tokenId, TokenType type) {
        return sessionRedis.opsForValue().get(prefix(type) + tokenId);
    }

    public void invalidateSession(String tokenId, TokenType type) {
        sessionRedis.delete(prefix(type) + tokenId);
    }

    public boolean hasRefreshSession(String tokenId) {
        return Boolean.TRUE.equals(
                stringRedis.hasKey("refresh:" + tokenId)
        );
    }


    private String prefix(TokenType type) {
        return type == TokenType.REFRESH ? "refresh:" : "access:";
    }

    /* ================= FAST TOKEN FLAG ================= */

    public void cacheTokenFlag(String tokenId, TokenType type, long ttlSeconds) {
        stringRedis.opsForValue().set(
                "auth:token:" + type.name().toLowerCase() + ":" + tokenId,
                "ACTIVE",
                ttlSeconds,
                TimeUnit.SECONDS
        );
    }

    public boolean isTokenCached(String tokenId, TokenType type) {
        return Boolean.TRUE.equals(
                stringRedis.hasKey("auth:token:" + type.name().toLowerCase() + ":" + tokenId)
        );
    }

    public void invalidateTokenFlag(String tokenId, TokenType type) {
        stringRedis.delete("auth:token:" + type.name().toLowerCase() + ":" + tokenId);
    }

    /* ================= MFA ================= */

    public void storeMfaChallenge(String tokenId, RedisMfaChallenge challenge, long ttlSeconds) {
        mfaRedis.opsForValue().set(
                "mfa:" + tokenId,
                challenge,
                ttlSeconds,
                TimeUnit.SECONDS
        );
    }

    public RedisMfaChallenge getMfaChallenge(String tokenId) {
        return mfaRedis.opsForValue().get("mfa:" + tokenId);
    }

    public void incrementMfaAttempts(String tokenId) {
        RedisMfaChallenge c = getMfaChallenge(tokenId);
        if (c == null) return;

        c.setAttempts(c.getAttempts() + 1);
        mfaRedis.opsForValue().set("mfa:" + tokenId, c);
    }

    public void consumeMfaChallenge(String tokenId) {
        mfaRedis.delete("mfa:" + tokenId);
    }

    /* ================= TRUST ================= */

    public boolean isTrustedDevice(String username, String deviceId) {
        return Boolean.TRUE.equals(
                stringRedis.hasKey("trust:device:" + username + ":" + deviceId)
        );
    }

    public void markTrustedDevice(String username, String deviceId) {
        stringRedis.opsForValue().set(
                "trust:device:" + username + ":" + deviceId,
                "1",
                30,
                TimeUnit.DAYS
        );
    }

    public boolean isKnownIp(String username, String ip) {
        return Boolean.TRUE.equals(
                stringRedis.hasKey("trust:ip:" + username + ":" + ip)
        );
    }

    public void markKnownIp(String username, String ip) {
        stringRedis.opsForValue().set(
                "trust:ip:" + username + ":" + ip,
                "1",
                30,
                TimeUnit.DAYS
        );
    }
}
