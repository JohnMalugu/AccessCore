package com.jcmlabs.AccessCore.Shared.Payload.ConfigurationProperties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "spring.mail")
public record EmailConfigurationProperties() {
}
