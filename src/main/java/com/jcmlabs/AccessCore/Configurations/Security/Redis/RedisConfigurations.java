package com.jcmlabs.AccessCore.Configurations.Security.Redis;

import com.jcmlabs.AccessCore.Shared.Entity.RedisAccessSession;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.jcmlabs.AccessCore.Shared.Entity.RedisMfaChallenge;

@Configuration
public class RedisConfigurations {

    // RedisTemplate for Access and Refresh tokens
    @Bean
    public RedisTemplate<String, RedisAccessSession> redisAccessSessionTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, RedisAccessSession> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // Keys as strings
        template.setKeySerializer(new StringRedisSerializer());

        // Values as JSON
        GenericJacksonJsonRedisSerializer valueSerializer = GenericJacksonJsonRedisSerializer.builder().build();
        template.setValueSerializer(valueSerializer);
        template.setHashValueSerializer(valueSerializer);

        template.afterPropertiesSet();
        return template;
    }

    // RedisTemplate for MFA challenges
    @Bean
    public RedisTemplate<String, RedisMfaChallenge> redisMfaChallengeTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, RedisMfaChallenge> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        template.setKeySerializer(new StringRedisSerializer());

        GenericJacksonJsonRedisSerializer valueSerializer = GenericJacksonJsonRedisSerializer.builder().build();
        template.setValueSerializer(valueSerializer);
        template.setHashValueSerializer(valueSerializer);

        template.afterPropertiesSet();
        return template;
    }

    // StringRedisTemplate for simple key-value operations (rate limiting, OTP, trust flags)
    // Spring Boot auto-configures this bean, so you may not need to define it manually.
    // @Bean
    // public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory connectionFactory) {
    //     return new StringRedisTemplate(connectionFactory);
    // }
}



