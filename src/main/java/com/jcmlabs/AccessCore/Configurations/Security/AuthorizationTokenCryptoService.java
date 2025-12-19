package com.jcmlabs.AccessCore.Configurations.Security;

import java.util.Base64;
import java.util.Optional;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.stereotype.Service;

import com.jcmlabs.AccessCore.Utilities.ConfigurationUtilities.TokenType;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthorizationTokenCryptoService {

    private final AuthorizationTokenConfigurationProperties properties;

    private SecretKeySpec accessKey;
    private SecretKeySpec refreshKey;
    private SecretKeySpec resetKey;

    @PostConstruct
    void init() {
        accessKey  = key(properties.getSigning().getAccessTokenSecret());
        refreshKey = key(properties.getSigning().getRefreshTokenSecret());
        resetKey   = key(properties.getSigning().getPasswordResetSecret());
    }

    private SecretKeySpec key(String secret) {
        return new SecretKeySpec(Base64.getUrlDecoder().decode(secret), "HmacSHA256");
    }

    private SecretKeySpec resolve(TokenType type) {
        return switch (type) {
            case ACCESS -> accessKey;
            case REFRESH -> refreshKey;
            case PASSWORD_RESET -> resetKey;
        };
    }

    public String sign(String value, TokenType type) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(resolve(type));
            return value + "." + Base64.getUrlEncoder().encodeToString(mac.doFinal(value.getBytes()));
        } catch (Exception e) {
            throw new IllegalStateException("Token signing failed", e);
        }
    }

    public Optional<String> verify(String signed, TokenType type) {
        if (!signed.contains(".")) return Optional.empty();
        String[] parts = signed.split("\\.", 2);
        return sign(parts[0], type).equals(signed)
                ? Optional.of(parts[0])
                : Optional.empty();
    }
}

