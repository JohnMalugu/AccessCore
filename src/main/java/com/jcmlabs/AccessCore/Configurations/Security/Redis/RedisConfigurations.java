package com.jcmlabs.AccessCore.Configurations.Security.Redis;

import com.jcmlabs.AccessCore.Shared.Entity.RedisAccessSession;
import com.jcmlabs.AccessCore.Shared.Entity.RedisVerificationToken;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.jcmlabs.AccessCore.Shared.Entity.RedisMfaChallenge;
import org.springframework.data.redis.serializer.JacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfigurations {

    @Bean
    public RedisTemplate<String, RedisAccessSession> redisAccessSessionTemplate(RedisConnectionFactory factory) {
        return createTypedTemplate(factory, RedisAccessSession.class);
    }

    @Bean
    public RedisTemplate<String, RedisMfaChallenge> redisMfaChallengeTemplate(RedisConnectionFactory factory) {
        return createTypedTemplate(factory, RedisMfaChallenge.class);
    }

    @Bean
    public RedisTemplate<String, RedisVerificationToken> redisVerificationTemplate(RedisConnectionFactory factory) {
        return createTypedTemplate(factory, RedisVerificationToken.class);
    }

    @Bean
    public RedisTemplate<String, RedisRegistrationVerification> redisRegistrationVerificationTemplate(RedisConnectionFactory factory) {
        return createTypedTemplate(factory, RedisRegistrationVerification.class);
    }

    /**
     * Helper method to create a typed RedisTemplate.
     * This avoids the "LinkedHashMap" casting issue by explicitly
     * binding the serializer to the target class.
     */
    private <T> RedisTemplate<String, T> createTypedTemplate(RedisConnectionFactory factory, Class<T> clazz) {
        RedisTemplate<String, T> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);

        // Standard String serializer for keys
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());

        // Modern Typed Jackson serializer for values (Spring 4.x style)
        JacksonJsonRedisSerializer<T> serializer = new JacksonJsonRedisSerializer<>(clazz);
        template.setValueSerializer(serializer);
        template.setHashValueSerializer(serializer);

        template.afterPropertiesSet();
        return template;
    }
}