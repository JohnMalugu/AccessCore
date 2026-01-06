package com.jcmlabs.AccessCore.Shared.ServiceImplementation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jcmlabs.AccessCore.Shared.Payload.Request.SmsDto;
import com.jcmlabs.AccessCore.Shared.Payload.ConfigurationProperties.SmsConfigurationProperties;
import com.jcmlabs.AccessCore.Shared.Service.SmsService;
import com.jcmlabs.AccessCore.Utilities.BaseResponse;
import com.jcmlabs.AccessCore.Utilities.ResponseCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class SmsServiceImplementation implements SmsService {

    private final RestClient smsRestClient;
    private final SmsConfigurationProperties props;
    private final ObjectMapper objectMapper;

    @Override
    public BaseResponse<Void> sendSms(SmsDto dto) {
        log.info("📩 [SMS] Preparing to send SMS to {}", dto.receiver());

        try {
            String messageId = UUID.randomUUID().toString();
            Instant now = Instant.now();

            Map<String, Object> payloadMap = Map.of(
                    "recipients", dto.receiver(),
                    "message", dto.message(),
                    "datetime",
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.systemDefault()).format(now),
                    "mobileServiceId", props.mobileServiceId(),
                    "senderId",
                    props.senderId(),
                    "messageId",
                    messageId
            );

            // 3️⃣ Serialize using ObjectMapper (NOW USED ✔)
            String payload = objectMapper.writeValueAsString(payloadMap);

            // 4️⃣ Generate HMAC hash
            String hash = generateHash(props.apiKey(), payload);

            // 5️⃣ Send HTTP request
            smsRestClient.post().uri(props.messageUrl()).contentType(MediaType.APPLICATION_JSON).header("hash", hash).header("sysId", props.systemId()).body(payload).retrieve().onStatus(HttpStatusCode::isError, (req, res) -> {
                throw new RuntimeException("SMS Provider Error: " + res.getStatusCode());
            }).toBodilessEntity();

            log.info("✅ [SMS] Queued successfully | messageId={}", messageId);
            return new BaseResponse<>(false, ResponseCode.SUCCESS, "SMS queued successfully with ID: " + messageId);

        } catch (Exception ex) {
            log.error("❌ [SMS] Sending failed", ex);
            return new BaseResponse<>(true, ResponseCode.EXCEPTION, "System was unable to process SMS request.");
        }
    }

    private String generateHash(String key, String data) throws Exception {
        Mac hmac = Mac.getInstance("HmacSHA256");
        hmac.init(new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
        return Base64.getEncoder().encodeToString(hmac.doFinal(data.getBytes(StandardCharsets.UTF_8)));
    }
}
