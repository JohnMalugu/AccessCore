package com.jcmlabs.AccessCore.Configurations.Security.Redis;


import com.jcmlabs.AccessCore.Shared.Entity.RedisAccessSession;
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

    private final StringRedisTemplate stringRedisTemplate;

    private final RedisTemplate<String, RedisAccessSession> accessSessionRedis;

    /* ================= RATE LIMITING ================= */

    public boolean allowForgotPassword(String username, String ip) {
        String key = "auth:rate:forgot:" + username + ":" + ip;
        Long count = stringRedisTemplate.opsForValue().increment(key);

        if (count != null && count == 1) {
            stringRedisTemplate.expire(key, Duration.ofMinutes(15));
        }
        return count != null && count <= 5;
    }

    public boolean allowChangePassword(String username, String ip) {
        String key = "auth:rate:change:" + username + ":" + ip;
        Long count = stringRedisTemplate.opsForValue().increment(key);

        if (count != null && count == 1) {
            stringRedisTemplate.expire(key, Duration.ofMinutes(10));
        }
        return count != null && count <= 3;
    }

    /* ================= RESET TOKEN ================= */

    public boolean markResetTokenUsed(String tokenId) {
        String key = "auth:reset:used:" + tokenId;
        Boolean set = stringRedisTemplate.opsForValue().setIfAbsent(key, "1", Duration.ofMinutes(30));
        return Boolean.TRUE.equals(set);
    }

    /* ================= USER SESSION TRACKING ================= */

    public void addUserSession(String username, String tokenId, long ttlSeconds) {
        String key = "user:sessions:" + username;
        stringRedisTemplate.opsForSet().add(key, tokenId);
        stringRedisTemplate.expire(key, ttlSeconds, TimeUnit.SECONDS);
    }

    public Set<String> getUserSessions(String username) {
        return stringRedisTemplate.opsForSet().members("user:sessions:" + username);
    }

    public void clearUserSessions(String username) {
        stringRedisTemplate.delete("user:sessions:" + username);
    }

    /* ================= ACCESS SESSION ================= */

    public void storeSession(RedisAccessSession session, long ttlSeconds) {
        String key = (session.getTokenType() == TokenType.REFRESH ? "refresh:" : "access:")
                + session.getTokenId();

        accessSessionRedis.opsForValue().set(key, session, ttlSeconds, TimeUnit.SECONDS);
    }


    public RedisAccessSession getSession(String tokenId, TokenType type) {
        String key = (type == TokenType.REFRESH ? "refresh:" : "access:") + tokenId;
        return accessSessionRedis.opsForValue().get(key);
    }

    public void invalidateAccessSession(String tokenId) {
        accessSessionRedis.delete("access:" + tokenId);
    }

    /* ================= FAST TOKEN FLAG ================= */

    public void cacheTokenFlag(String tokenId, String tokenType, long ttlSeconds) {
        stringRedisTemplate.opsForValue().set("auth:token:" + tokenType.toLowerCase() + ":" + tokenId, "ACTIVE", ttlSeconds, TimeUnit.SECONDS);
    }

    public boolean isTokenCached(String tokenId, String tokenType) {
        return Boolean.TRUE.equals(stringRedisTemplate.hasKey("auth:token:" + tokenType.toLowerCase() + ":" + tokenId));
    }

    public void invalidateTokenFlag(String tokenId, String tokenType) {
        stringRedisTemplate.delete("auth:token:" + tokenType.toLowerCase() + ":" + tokenId);
    }

    public boolean hasRefreshSession(String tokenId) {
        return Boolean.TRUE.equals(stringRedisTemplate.hasKey("refresh:" + tokenId));
    }

    public void invalidateSession(String tokenId, TokenType type) {
        stringRedisTemplate.delete(
                (type == TokenType.REFRESH ? "refresh:" : "access:") + tokenId
        );
    }

    public void storeMfaChallenge(
            String tokenId,
            String username,
            String ip,
            String deviceId,
            long ttlSeconds
    ) {
        stringRedisTemplate.opsForHash().put("mfa:" + tokenId, "user", username);
        stringRedisTemplate.opsForHash().put("mfa:" + tokenId, "ip", ip);
        stringRedisTemplate.opsForHash().put("mfa:" + tokenId, "device", deviceId);
        stringRedisTemplate.expire("mfa:" + tokenId, ttlSeconds, TimeUnit.SECONDS);
    }

    public boolean isTrustedDevice(String username, String deviceId) {
        return Boolean.TRUE.equals(
                stringRedisTemplate.hasKey("auth:device:trusted:" + username + ":" + deviceId)
        );
    }

    public void markTrustedDevice(String username, String deviceId) {
        stringRedisTemplate.opsForValue().set(
                "auth:device:trusted:" + username + ":" + deviceId,
                "1",
                30, TimeUnit.DAYS
        );
    }

    public boolean isKnownIp(String username, String ip) {
        return Boolean.TRUE.equals(
                stringRedisTemplate.hasKey("auth:ip:known:" + username + ":" + ip)
        );
    }

    public void markKnownIp(String username, String ip) {
        stringRedisTemplate.opsForValue().set(
                "auth:ip:known:" + username + ":" + ip,
                "1",
                30, TimeUnit.DAYS
        );
    }

    public long incrementMfaAttempts(String tokenId) {
        String key = "auth:mfa:attempts:" + tokenId;
        Long count = stringRedisTemplate.opsForValue().increment(key);

        if (count != null && count == 1) {
            stringRedisTemplate.expire(key, 5, TimeUnit.MINUTES);
        }
        return count == null ? 0 : count;
    }



}
