package com.jcmlabs.AccessCore.Configurations.Security;

import java.time.Duration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import com.jcmlabs.AccessCore.Utilities.ConfigurationUtilities.TokenType;

import lombok.Getter;
import lombok.Setter;

@Configuration
@ConfigurationProperties(prefix = "token")
@Getter
@Setter
public class AuthorizationTokenConfigurationProperties {

    private Signing signing = new Signing();
    private Lifespan lifespan = new Lifespan();

    @Getter
    @Setter
    public static class Signing {
        private String accessTokenSecret;
        private String refreshTokenSecret;
        private String passwordResetSecret;
        private String mfaSecret;
    }

    @Getter
    @Setter
    public static class Lifespan {
        private Duration accessTokenLifespan;
        private Duration refreshTokenLifespan;
        private Duration passwordResetLifespan;

        public Duration resolve(TokenType type) {
            return switch (type) {
                case ACCESS -> accessTokenLifespan;
                case REFRESH -> refreshTokenLifespan;
                case PASSWORD_RESET, MFA_CHALLENGE -> passwordResetLifespan;
            };
        }
    }
}
