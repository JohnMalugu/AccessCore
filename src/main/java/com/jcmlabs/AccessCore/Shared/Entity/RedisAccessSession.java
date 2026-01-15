package com.jcmlabs.AccessCore.Shared.Entity;

import com.jcmlabs.AccessCore.Utilities.ConfigurationUtilities.TokenType;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RedisAccessSession {
    private String tokenId;
    private String username;
    private String ip;
    private TokenType tokenType;
}

