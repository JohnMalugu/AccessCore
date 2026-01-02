package com.jcmlabs.AccessCore.Configurations.Security.Events;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TokenEventPublisher {

    private final KafkaTemplate<String, AuthEvent> kafkaTemplate;

    private static final String TOPIC = "auth-events";

    public void publish(AuthEvent event) {
        kafkaTemplate.send(TOPIC, event.username(), event);
    }
}
