package com.jcmlabs.AccessCore.Shared.Payload.ConfigurationProperties;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * <h3>Service Provider Configuration Properties</h3>
 *
 * <p>This class is implemented as a <b>Java Record</b> using {@link ConfigurationProperties}
 * to align with modern Spring Boot (3.x+) best practices for the following reasons:</p>
 *
 * <ul>
 * <li><b>Prefix-Based Mapping:</b> The {@code prefix} attribute (e.g., {@code "sms.provider"})
 * acts as a namespace. It tells Spring to look for any property in {@code application.properties}
 * <b>starting with that specific prefix</b> (e.g., {@code sms.provider.apiKey}). This allows
 * you to switch providers (like Twilio or BeemAfrica) simply by changing the values assigned
 * to that prefix.</li>
 *
 * <li><b>Centralized Management:</b> Instead of scattering multiple {@code @Value}
 * annotations across different services, all related settings are bound to this single object.
 * This makes the code significantly easier to maintain and navigate.</li>
 *
 * <li><b>Startup Validation (Fail-Fast):</b> By using {@link Validated}, Spring checks
 * these properties when the app starts. If an {@link NotBlank} field is missing in the
 * {@code .properties} file, the app will crash immediately with a clear error, preventing
 * runtime failures during SMS transactions.</li>
 *
 * <li><b>Immutability:</b> Records are immutable by design. Once Spring loads these
 * settings, they cannot be modified by any other part of the application, ensuring
 * thread-safety for sensitive credentials.</li>
 *
 * <li><b>Relaxed Binding:</b> Spring automatically handles the conversion between
 * standard configuration formats (like {@code message-url} or {@code MESSAGE_URL})
 * and these camelCase fields ({@code messageUrl}), reducing manual mapping errors.</li>
 * </ul>
 */
@Validated
@ConfigurationProperties(prefix = "mgov.message")
public record SmsConfigurationProperties(
        @NotBlank String messageUrl,
        @NotBlank String apiKey,
        @NotBlank String mobileServiceId,
        @NotBlank String systemId,
        @NotBlank String senderId,
        String voteCode
) {}