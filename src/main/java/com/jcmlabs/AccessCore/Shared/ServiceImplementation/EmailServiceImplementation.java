package com.jcmlabs.AccessCore.Shared.ServiceImplementation;

import com.jcmlabs.AccessCore.Shared.Payload.Request.EmailDto;
import com.jcmlabs.AccessCore.Shared.Service.EmailService;
import com.jcmlabs.AccessCore.Utilities.BaseResponse;
import com.jcmlabs.AccessCore.Utilities.ResponseCode;
import jakarta.mail.internet.MimeMessage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImplementation implements EmailService {

    private final JavaMailSender javaMailSender;

    @Value("${spring.mail.properties.mail.smtp.from}")
    private String sender;
    @Override
    public BaseResponse<Void> sendEmail(EmailDto dto) {
        log.info("📧 [EmailService] Preparing to send email...");

        try{
            if (dto.recipient() == null || dto.recipient().isBlank()) {
                log.error("❌ Recipient email is null or empty");
                return new BaseResponse<>(true,ResponseCode.REQUIRED_FIELD, "Recipient email is required.");
            }

            String safeSender = sender != null ? sender : "";
            if (safeSender.isEmpty()) {
                log.warn("⚠️ 'sender' value from properties is null or empty");
            }

            String safeSubject = dto.subject() != null ? dto.subject() : "(no subject)";

            String safeBody = dto.body() != null ? dto.body() : "";

            String safeRecipient = dto.recipient();

            log.info("📨 Sending email: to={}, subject={}, html={}",
                    safeRecipient, safeSubject, dto.isHtml());

            MimeMessage mime = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mime, true, "UTF-8");

            helper.setFrom(safeSender);
            helper.setTo(safeRecipient);
            helper.setSubject(safeSubject);
            helper.setText(safeBody, dto.isHtml());

            log.info("📤 Dispatching email...");
            javaMailSender.send(mime);

            log.info("✅ Email sent successfully to {}", safeRecipient);
            return new BaseResponse<>(false,ResponseCode.SUCCESS,"Email sent successfully");
        }
        catch (Exception exception){
            log.error("❌ Email sending failed: ", exception);
            return new BaseResponse<>(true,ResponseCode.EXCEPTION,"Email sending failed");
        }

    }
}
