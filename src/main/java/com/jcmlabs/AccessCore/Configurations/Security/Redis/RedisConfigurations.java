package com.jcmlabs.AccessCore.Configurations.Security.Redis;

import com.jcmlabs.AccessCore.Shared.Entity.RedisAccessSession;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfigurations {

    @Bean
    public RedisTemplate<String, RedisAccessSession> redisAccessSessionTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, RedisAccessSession> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        template.setKeySerializer(new StringRedisSerializer());

        GenericJacksonJsonRedisSerializer valueSerializer = GenericJacksonJsonRedisSerializer.builder().build();

        template.setValueSerializer(valueSerializer);
        template.setHashValueSerializer(valueSerializer);

        template.afterPropertiesSet();
        return template;
    }
}


