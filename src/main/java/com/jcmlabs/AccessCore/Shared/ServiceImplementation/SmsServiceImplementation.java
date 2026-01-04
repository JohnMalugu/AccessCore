package com.jcmlabs.AccessCore.Shared.ServiceImplementation;

import com.jcmlabs.AccessCore.Shared.Payload.Request.SmsDto;
import com.jcmlabs.AccessCore.Shared.Payload.Request.SmsProperties;
import com.jcmlabs.AccessCore.Shared.Service.SmsService;
import com.jcmlabs.AccessCore.Utilities.BaseResponse;
import com.jcmlabs.AccessCore.Utilities.ResponseCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import tools.jackson.databind.ObjectMapper;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class SmsServiceImplementation implements SmsService {
    private final SmsProperties props;
    private final RestClient restClient = RestClient.create(); // In production, inject a Bean
    private final ObjectMapper mapper;


    @Override
    public BaseResponse<Void> sendSms(SmsDto dto) {
        try{
            String messageId = UUID.randomUUID().toString();
            Map<String, Object> body = Map.of(
                    "recipients", dto.receiver(),
                    "message", dto.message(),
                    "datetime", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                    "mobileServiceId", props.mobileServiceId(),
                    "senderId", props.senderId(),
                    "messageId", messageId
            );

            String payload = mapper.writeValueAsString(body);
            String hash = generateHash(props.apiKey(), payload);

            restClient.post()
                    .uri(props.messageUrl())
                    .header("hash", hash)
                    .header("sysId", props.systemId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(payload)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, (req, res) -> {
                        throw new RuntimeException("External API Error: " + res.getStatusCode());
                    })
                    .toBodilessEntity();
            return new BaseResponse<>(false, ResponseCode.SUCCESS,"SMS queued successfully with ID: " + messageId);
        }
        catch (Exception exception){
            log.error("SMS Failure: ", exception);
            return new BaseResponse<>(true,ResponseCode.EXCEPTION,"System was unable to process SMS request.");
    }
    }



    private String generateHash(String key, String data) throws Exception {
        Mac hmac = Mac.getInstance("HmacSHA256");
        hmac.init(new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
        return Base64.getEncoder().encodeToString(hmac.doFinal(data.getBytes(StandardCharsets.UTF_8)));
    }
}