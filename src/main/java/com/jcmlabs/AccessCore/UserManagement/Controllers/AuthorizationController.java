package com.jcmlabs.AccessCore.UserManagement.Controllers;


import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jcmlabs.AccessCore.Configurations.Security.AuthorizationServiceHelper;
import com.jcmlabs.AccessCore.UserManagement.Payload.LoginRequestInput;
import com.jcmlabs.AccessCore.Utilities.BaseResponse;
import com.jcmlabs.AccessCore.Utilities.RequestClientIpUtility;
import com.jcmlabs.AccessCore.Utilities.ResponseCode;
import com.jcmlabs.AccessCore.Utilities.ConfigurationUtilities.AuthTokenResponse;
import com.jcmlabs.AccessCore.Utilities.ConfigurationUtilities.RefreshTokenRequest;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthorizationController {
    private final AuthorizationServiceHelper authorizationServiceHelper;

    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AuthTokenResponse> login(@RequestBody LoginRequestInput request,HttpServletRequest httpRequest) {
        String clientIp = RequestClientIpUtility.getClientIpAddress(httpRequest);
        AuthTokenResponse tokens = authorizationServiceHelper.login(request.username(), request.password(), clientIp,request.scopes());
        return ResponseEntity.ok(tokens);
    }

    @PostMapping(value = "/refresh-token", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AuthTokenResponse> refreshToken(@RequestBody RefreshTokenRequest request,HttpServletRequest httpRequest) {
        String clientIp = RequestClientIpUtility.getClientIpAddress(httpRequest);
        AuthTokenResponse tokens = authorizationServiceHelper.refresh(request.getRefreshToken(), clientIp);
        return ResponseEntity.ok(tokens);
    }

    @PostMapping(value = "/logout",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseResponse<Void>> logout(@RequestHeader("Authorization") String authorizationHeader,HttpServletRequest request) {
        String clientIp = RequestClientIpUtility.getClientIpAddress(request);
        authorizationServiceHelper.revokeToken(authorizationHeader, clientIp);
        return ResponseEntity.ok(new BaseResponse<>(true, ResponseCode.SUCCESS,"Logged out successfully"));
    }


}
