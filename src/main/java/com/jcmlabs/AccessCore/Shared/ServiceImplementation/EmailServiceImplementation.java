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
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImplementation implements EmailService {

    private final JavaMailSender javaMailSender;

    @Value("${spring.mail.properties.mail.smtp.from}")
    private String sender;

    /**
     * Sends an email using SMTP.
     *
     * <p>
     * This method is wrapped by Spring Retry via {@link Retryable}.
     * Any exception thrown from this method will trigger a retry
     * according to the configured retry policy.
     * </p>
     *
     * <p>
     * IMPORTANT:
     * This method MUST rethrow the exception when a failure occurs.
     * Swallowing the exception will disable retry and recovery logic.
     * </p>
     */
    @Retryable(
            retryFor = Exception.class,
            maxAttempts = 3,
            backoff = @Backoff(delay = 2000)
    )
    @Override
    public BaseResponse<Void> sendEmail(EmailDto dto) {

        log.info("📧 Sending email to {}", dto.recipient());

        try {
            MimeMessage mime = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mime, true, "UTF-8");

            helper.setFrom(sender);
            helper.setTo(dto.recipient());
            helper.setSubject(dto.subject());
            helper.setText(dto.body(), dto.isHtml());

            javaMailSender.send(mime);

            return new BaseResponse<>(false, ResponseCode.SUCCESS, "Email sent");

        } catch (Exception e) {
            log.error("❌ SMTP failure", e);

            // REQUIRED:
            // The exception must be rethrown so that Spring Retry can:
            // 1. Retry the operation
            // 2. Eventually invoke the @Recover method if retries are exhausted
            throw new RuntimeException(e);
        }
    }

    /**
     * Recovery method invoked by Spring Retry AFTER all retry attempts fail.
     *
     * <p>
     * ⚠️ This method is NOT called directly anywhere in the codebase.
     * It is invoked automatically by Spring Retry through AOP.
     * </p>
     *
     * <p>
     * IDEs may incorrectly mark this method as "unused".
     * DO NOT DELETE IT.
     * Removing this method will cause retry exhaustion to rethrow
     * the original exception instead of returning a graceful response.
     * </p>
     *
     * <p>
     * When invoked:
     * <ul>
     *   <li>SMTP is considered unavailable</li>
     *   <li>Retries have already been exhausted</li>
     *   <li>A controlled failure response is returned</li>
     * </ul>
     * </p>
     */
    @Recover
    public BaseResponse<Void> recover(RuntimeException ex, EmailDto dto) {
        log.error("📛 SMTP DOWN after retries for {}", dto.recipient(), ex);
        return new BaseResponse<>(true, ResponseCode.EXCEPTION, "SMTP unavailable");
    }
}
