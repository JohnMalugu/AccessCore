package com.jcmlabs.AccessCore.Configurations.RabbitMQ;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jcmlabs.AccessCore.Shared.Entity.NotificationEntity;
import com.jcmlabs.AccessCore.Shared.Entity.RabbitmqMessage;
import com.jcmlabs.AccessCore.Shared.Enums.NotificationStatus;
import com.jcmlabs.AccessCore.Shared.Payload.ConfigurationProperties.RabbitmqConfigurationProperties;
import com.jcmlabs.AccessCore.Shared.Payload.Request.NotificationDto;
import com.jcmlabs.AccessCore.Shared.Repository.RabbitmqMessageRepository;
import com.jcmlabs.AccessCore.Shared.Service.NotificationService;
import com.jcmlabs.AccessCore.Utilities.BaseResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class RabbitmqProducer {

    private final ObjectMapper objectMapper;
    private final RabbitTemplate rabbitTemplate;
    private final RabbitmqConfigurationProperties rabbitProps;
    private final RabbitmqMessageRepository rabbitMQMessageRepository;
    private final NotificationService notificationService;

    @Transactional
    public boolean sendMessageToRabbitMQ(NotificationDto input) {
        log.info("📤 [RabbitMQ] Processing request for: {}", input.fullName());

        BaseResponse<NotificationEntity> createdResponse = notificationService.createNotification(input);
        if (!createdResponse.getResponse().getSuccess() || createdResponse.getData() == null) {
            log.error("❌ [DB] Failed to create notification record.");
            return false;
        }

        NotificationEntity entity = createdResponse.getData();
        boolean isQueued = false;
        String messageJson = "";

        try {
            NotificationDto payload = input.toBuilder()
                    .uuid(entity.getUuid())
                    .createdAt(entity.getCreatedAt())
                    .build();

            messageJson = objectMapper.writeValueAsString(payload);

            rabbitTemplate.convertAndSend(
                    rabbitProps.template().exchange(),
                    rabbitProps.template().routingKey(),
                    messageJson
            );

            isQueued = true;
            log.info("🚀 [RabbitMQ] Message queued with UUID: {}", entity.getUuid());

        } catch (Exception e) {
            log.error("❌ [RabbitMQ] Publish failed for UUID: {}", entity.getUuid(), e);
        }

        saveMqLog(messageJson, isQueued);
        updateStatus(entity.getUuid(), isQueued ? NotificationStatus.QUEUED : NotificationStatus.FAILED);

        return isQueued;
    }

    private void saveMqLog(String jsonPayload, boolean success) {
        RabbitmqMessage mqLog = new RabbitmqMessage();
        mqLog.setPayload(jsonPayload);
        mqLog.setIsSent(success);
        mqLog.setCreatedAt(java.time.Instant.now());
        rabbitMQMessageRepository.save(mqLog);
    }

    private void updateStatus(String uuid, NotificationStatus status) {
        NotificationDto update = NotificationDto.builder()
                .uuid(uuid)
                .status(status)
                .build();
        notificationService.updateNotificationStatus(update);
    }
}