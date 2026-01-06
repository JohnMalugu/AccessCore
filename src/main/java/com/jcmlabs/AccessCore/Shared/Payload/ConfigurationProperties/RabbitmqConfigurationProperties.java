package com.jcmlabs.AccessCore.Shared.Payload.ConfigurationProperties;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * <h3>RabbitMQ Configuration Properties</h3>
 *
 * <p>This configuration uses <b>Nested Records</b> to mirror the hierarchical structure
 * of {@code application.properties} (e.g., {@code spring.rabbitmq.template.*}).</p>
 *
 * <ul>
 * <li><b>Why Nested?</b> It maintains logical grouping and namespacing. Without nesting,
 * you would have a "Flat Record" where dots are replaced by CamelCase (e.g.,
 * {@code templateExchange}).</li>
 * * <li><b>Flat Comparison:</b> A flat approach would require field names like
 * {@code templateDefaultReceiveQueue}, losing the clean separation between connection
 * settings and messaging templates.</li>
 * * <li><b>Validation:</b> Uses {@link Validated} with {@link NotBlank} to ensure
 * the application "Fails Fast" during startup if configuration is missing.</li>
 * </ul>
 * * <p><b>Alternative (Flat structure mapping):</b><br>
 * If properties were flat: {@code spring.rabbitmq.templateExchange=my_exchange}<br>
 * Record field would be: {@code String templateExchange}</p>
 */
@Validated
@ConfigurationProperties(prefix = "spring.rabbitmq")
public record RabbitmqConfigurationProperties(
        @NotBlank String host,
        @NotNull Integer port,
        @NotBlank String username,
        @NotBlank String password,
        TemplateProperties template
) {
    /**
     * Grouped properties for the RabbitTemplate (spring.rabbitmq.template.*)
     */
    public record TemplateProperties(
            @NotBlank String defaultReceiveQueue,
            @NotBlank String exchange,
            @NotBlank String routingKey
    ) {}
}