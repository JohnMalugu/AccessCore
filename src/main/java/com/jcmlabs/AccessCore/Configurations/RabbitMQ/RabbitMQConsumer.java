package com.jcmlabs.AccessCore.Configurations.RabbitMQ;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jcmlabs.AccessCore.Shared.Enums.EmailType;
import com.jcmlabs.AccessCore.Shared.Enums.NotificationStatus;
import com.jcmlabs.AccessCore.Shared.Enums.NotificationType;
import com.jcmlabs.AccessCore.Shared.Payload.Request.EmailDto;
import com.jcmlabs.AccessCore.Shared.Payload.Request.NotificationDto;
import com.jcmlabs.AccessCore.Shared.Payload.Request.SmsDto;
import com.jcmlabs.AccessCore.Shared.Service.EmailService;
import com.jcmlabs.AccessCore.Shared.Service.NotificationService;
import com.jcmlabs.AccessCore.Shared.Service.SmsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
@Slf4j
public class RabbitMQConsumer {

    private final SmsService smsService;
    private final EmailService emailService;
    private final NotificationService notificationService;
    private final ObjectMapper objectMapper;
    private final ResourceLoader resourceLoader;

    private final Map<String, String> templateCache = new ConcurrentHashMap<>();

    @RabbitListener(queues = "${spring.rabbitmq.template.default-receive-queue}")
    public void receiveMessage(String message) {
        log.info("📥 [RabbitMQ] Processing message...");
        NotificationDto notification = null;

        try {
            notification = objectMapper.readValue(message, NotificationDto.class);

            if (notification.notificationType() == NotificationType.EMAIL
                    && notification.emailType() == null) {

                log.error("❌ EMAIL notification missing emailType. UUID={}", notification.uuid());
                updateDbStatus(notification.uuid(), NotificationStatus.FAILED);
                throw new AmqpRejectAndDontRequeueException("Invalid EMAIL payload");
            }

            switch (notification.notificationType()) {
                case EMAIL -> processEmail(notification);
                case SMS -> processSms(notification);
                default -> {
                    log.error("❌ Unsupported type: {}. Sending to DLQ.", notification.notificationType());
                    updateDbStatus(notification.uuid(), NotificationStatus.FAILED);
                    throw new AmqpRejectAndDontRequeueException("Unsupported type");
                }
            }

            updateDbStatus(notification.uuid(), NotificationStatus.SENT);
            log.info("✅ Notification SENT successfully: {}", notification.uuid());

        } catch (JsonProcessingException e) {
            log.error("❌ [FATAL] Invalid JSON structure. Cannot recover. Raw={}", message);
            throw new AmqpRejectAndDontRequeueException(e);

        } catch (AmqpRejectAndDontRequeueException e) {
            if (notification != null) updateDbStatus(notification.uuid(), NotificationStatus.FAILED);
            throw e;

        } catch (Exception e) {
            log.warn("⚠️ [RETRY] Delivery attempt failed for UUID: {}. Error: {}", (notification != null ? notification.uuid() : "Unknown"), e.getMessage());

            throw new RuntimeException(e);
        }
    }

    private void processSms(NotificationDto n) {
        SmsDto sms = SmsDto.builder().receiver(n.phoneTo()).message(n.message()).date(n.createdAt()).build();
        smsService.sendSms(sms);
    }

    private void processEmail(NotificationDto n) throws Exception {
        String template = getTemplate(n.emailType());
        String body = template
                .replace("${fullName}", escapeHtml(n.fullName()))
                .replace("${message}", n.message())
                .replace("${currentDate}", Instant.now().toString());

        EmailDto email = EmailDto.builder().recipient(n.emailTo()).subject(n.subject() != null ? n.subject() : "Notification Update").body(body).isHtml(true).build();
        emailService.sendEmail(email);
    }

    private void updateDbStatus(String uuid, NotificationStatus status) {
        if (uuid == null || uuid.isBlank()) return;

        try {
            notificationService.updateNotificationStatus(NotificationDto.builder().uuid(uuid).status(status).build());
        } catch (Exception e) {
            log.error("❌ Failed to update DB status for UUID: {} to {}", uuid, status, e);
        }
    }

    private String getTemplate(EmailType type) {

        if (type == null) {
            throw new IllegalArgumentException("EmailType must not be null");
        }

        String path = "templates/" + type.name().toLowerCase() + ".html";

        return templateCache.computeIfAbsent(path, p -> {
            try {
                Resource resource = resourceLoader.getResource("classpath:" + p);
                if (!resource.exists()) throw new FileNotFoundException(p);
                return new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            } catch (Exception e) {
                throw new RuntimeException("Template load error: " + p, e);
            }
        });
    }


    private String escapeHtml(String input) {
        if (input == null) return "";
        return input
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}