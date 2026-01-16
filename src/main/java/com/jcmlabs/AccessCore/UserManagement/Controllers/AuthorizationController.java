package com.jcmlabs.AccessCore.UserManagement.Controllers;


import com.jcmlabs.AccessCore.UserManagement.Payload.Request.ChangePasswordRequest;
import com.jcmlabs.AccessCore.UserManagement.Payload.Request.MfaVerifyRequestDto;
import com.jcmlabs.AccessCore.UserManagement.Payload.Request.UpdatePasswordRequestDto;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jcmlabs.AccessCore.Configurations.Security.AuthorizationServiceHelper;
import com.jcmlabs.AccessCore.UserManagement.Payload.Request.LoginRequestDto;
import com.jcmlabs.AccessCore.Utilities.BaseResponse;
import com.jcmlabs.AccessCore.Utilities.RequestClientIpUtility;
import com.jcmlabs.AccessCore.Utilities.ResponseCode;
import com.jcmlabs.AccessCore.Utilities.ConfigurationUtilities.AuthTokenResponse;
import com.jcmlabs.AccessCore.Utilities.ConfigurationUtilities.RefreshTokenRequest;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

import static com.jcmlabs.AccessCore.Utilities.ResponseCode.SUCCESS;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthorizationController {
    private final AuthorizationServiceHelper authorizationServiceHelper;

    @PostMapping("/login")
    public ResponseEntity<AuthTokenResponse> login(@RequestBody LoginRequestDto request, @RequestHeader("X-Device-Id") String deviceId, HttpServletRequest httpRequest) {
        String clientIp = RequestClientIpUtility.getClientIpAddress(httpRequest);
        AuthTokenResponse tokens = authorizationServiceHelper.login(request.username(), request.password(), clientIp, deviceId, request.scopes());
        return ResponseEntity.ok(tokens);
    }

    @PostMapping(value = "/refresh-token", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AuthTokenResponse> refreshToken(@RequestBody RefreshTokenRequest request,HttpServletRequest httpRequest) {
        String clientIp = RequestClientIpUtility.getClientIpAddress(httpRequest);
        AuthTokenResponse tokens = authorizationServiceHelper.refresh(request.getRefreshToken(), clientIp);
        return ResponseEntity.ok(tokens);
    }

    @PostMapping("/logout")
    public ResponseEntity<BaseResponse<Void>> logout(@RequestHeader("Authorization") String authorization, HttpServletRequest request) {
        String clientIp = RequestClientIpUtility.getClientIpAddress(request);
        authorizationServiceHelper.revokeToken(authorization, clientIp);
        return ResponseEntity.ok(new BaseResponse<>(true, SUCCESS, "Logged out successfully"));
    }


    @PostMapping(value = "/forgot-password",consumes = MediaType.APPLICATION_JSON_VALUE,produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseResponse<Void>> forgotPassword(@RequestBody UpdatePasswordRequestDto request, HttpServletRequest httpRequest){
        String clientIP = RequestClientIpUtility.getClientIpAddress(httpRequest);
        authorizationServiceHelper.forgotPassword(request.username(), clientIP);
        return ResponseEntity.ok(new BaseResponse<>(true, SUCCESS,"If an account exists, a password reset link has been sent"));
    }

    @PostMapping(value = "/reset-password",consumes = MediaType.APPLICATION_JSON_VALUE,produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseResponse<Void>> resetPassword(@RequestBody UpdatePasswordRequestDto requestInput, HttpServletRequest httpServletRequest){
        String clientIP = RequestClientIpUtility.getClientIpAddress(httpServletRequest);
        authorizationServiceHelper.resetPassword(requestInput.token(),requestInput.password(),requestInput.confirmPassword(),clientIP);
        return ResponseEntity.ok(new BaseResponse<>(true, SUCCESS,"Password reset successfully"));
    }

    @PostMapping("/change-password")
    public ResponseEntity<BaseResponse<Void>> changePassword(Authentication authentication, @RequestBody ChangePasswordRequest request, HttpServletRequest httpRequest) {
        String clientIp = RequestClientIpUtility.getClientIpAddress(httpRequest);
        authorizationServiceHelper.changePassword(authentication.getName(), request.currentPassword(), request.newPassword(), request.confirmPassword(), clientIp);
        return ResponseEntity.ok(new BaseResponse<>(true, SUCCESS, "Password changed"));
    }

    @PostMapping("/mfa/verify")
    public ResponseEntity<AuthTokenResponse> verifyMfa(@RequestBody MfaVerifyRequestDto request, HttpServletRequest http) {
        String ip = RequestClientIpUtility.getClientIpAddress(http);
        return ResponseEntity.ok(authorizationServiceHelper.verifyMfa(request.mfaToken(), request.code(), request.deviceId(), ip));
    }

}
