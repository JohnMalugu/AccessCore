package com.jcmlabs.AccessCore.Configurations.Security;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.Optional;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.jcmlabs.AccessCore.Utilities.ConfigurationUtilities.TokenType;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthorizationTokenCryptoService {

    private final AuthorizationTokenConfigurationProperties properties;

    @Value("${security.otp.pepper}")
    private String otpPepper;

    private SecretKeySpec accessKey;
    private SecretKeySpec refreshKey;
    private SecretKeySpec resetKey;
    private SecretKeySpec mfaKey;

    @PostConstruct
    void init() {
        accessKey  = key(properties.getSigning().getAccessTokenSecret());
        refreshKey = key(properties.getSigning().getRefreshTokenSecret());
        resetKey   = key(properties.getSigning().getPasswordResetSecret());
        mfaKey     = key(properties.getSigning().getMfaSecret());
    }

    private SecretKeySpec key(String secret) {
        return new SecretKeySpec(Base64.getUrlDecoder().decode(secret), "HmacSHA256");
    }

    private SecretKeySpec resolve(TokenType type) {
        return switch (type) {
            case ACCESS -> accessKey;
            case REFRESH -> refreshKey;
            case PASSWORD_RESET -> resetKey;
            case MFA_CHALLENGE -> mfaKey;
        };
    }

    public String sign(String value, TokenType type) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(resolve(type));
            return value + "." + Base64.getUrlEncoder().encodeToString(mac.doFinal(value.getBytes(StandardCharsets.UTF_8)));
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

    /* ===== OTP ===== */

    public String hashOtp(String otp) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return Base64.getEncoder().encodeToString(
                    digest.digest((otp + otpPepper).getBytes(StandardCharsets.UTF_8))
            );
        } catch (Exception e) {
            throw new IllegalStateException("OTP hashing failed", e);
        }
    }

    public boolean verifyOtp(String rawOtp, String storedHash) {
        return MessageDigest.isEqual(
                hashOtp(rawOtp).getBytes(StandardCharsets.UTF_8),
                storedHash.getBytes(StandardCharsets.UTF_8)
        );
    }
}
