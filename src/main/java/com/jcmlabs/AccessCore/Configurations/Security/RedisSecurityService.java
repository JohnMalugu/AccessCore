package com.jcmlabs.AccessCore.Configurations.Security;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisSecurityService {

    private final StringRedisTemplate redisTemplate;

    // Rate limiting for forgot password
    public boolean allowForgotPassword(String username, String ip) {
        String key = "auth:rate:forgot:" + username + ":" + ip;
        Long count = redisTemplate.opsForValue().increment(key);

        if (count == 1) {
            redisTemplate.expire(key, Duration.ofMinutes(15));
        }

        return count <= 5; // Max 5 attempts per 15 minutes
    }

    // Prevent replay of reset tokens
    public boolean markResetTokenUsed(String tokenId) {
        String key = "auth:reset:used:" + tokenId;
        Boolean set = redisTemplate.opsForValue().setIfAbsent(key, "1", Duration.ofMinutes(30));
        return Boolean.TRUE.equals(set);
    }

    // Cache active tokens for fast validation
    public void cacheToken(String tokenId, String tokenType, long ttlSeconds) {
        String key = "auth:token:" + tokenType.toLowerCase() + ":" + tokenId;
        redisTemplate.opsForValue().set(key, "ACTIVE", Duration.ofSeconds(ttlSeconds));
    }

    // Invalidate cached token (on logout/revoke)
    public void invalidateToken(String tokenId, String tokenType) {
        String key = "auth:token:" + tokenType.toLowerCase() + ":" + tokenId;
        redisTemplate.delete(key);
    }

    // Check if token is cached (fast path)
    public boolean isTokenCached(String tokenId, String tokenType) {
        String key = "auth:token:" + tokenType.toLowerCase() + ":" + tokenId;
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }
}