package com.jcmlabs.AccessCore.Configurations.Security.KafkaEvents;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TokenEventPublisher {

    private final KafkaTemplate<String, AuthEvent> kafkaTemplate;

    private static final String TOPIC = "auth-events";

    public void publish(AuthEvent event) {
        try {
            kafkaTemplate.send(TOPIC, event.username(), event);
        } catch (Exception e) {
            log.warn("Kafka publish failed for event {}: {}", event.eventType(), e.getMessage());
        }
    }
}
