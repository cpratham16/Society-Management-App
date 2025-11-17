package com.society.management.controller;

import com.society.management.dto.request.*;
import com.society.management.dto.response.AdminRegistrationInitResponse;
import com.society.management.dto.response.ApiResponse;
import com.society.management.dto.response.LoginResponse;
import com.society.management.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"})
@Slf4j
@Tag(name = "Authentication", description = "User authentication endpoints")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    @Operation(summary = "Register a new user", description = "Create a new user account with email verification")
    public ResponseEntity<ApiResponse<Void>> register(@RequestBody @Valid RegisterRequest request) {
        log.info("Register endpoint called for: {}", request.getEmail());
        ApiResponse<Void> response = authService.register(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/verify-otp")
    @Operation(summary = "Verify OTP", description = "Verify email with OTP code received")
    public ResponseEntity<ApiResponse<Void>> verifyOtp(@RequestBody @Valid VerifyOtpRequest request) {
        log.info("Verify OTP endpoint called for: {}", request.getEmail());
        ApiResponse<Void> response = authService.verifyOtp(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/admin/initiate")
    @Operation(summary = "Initiate admin registration", description = "Validate admin code and send OTP")
    public ResponseEntity<ApiResponse<AdminRegistrationInitResponse>> initiateAdminRegistration(
            @RequestBody @Valid AdminRegistrationInitiateRequest request) {
        log.info("Admin registration initiate called for: {}", request.getEmail());
        ApiResponse<AdminRegistrationInitResponse> response = authService.initiateAdminRegistration(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/admin/complete")
    @Operation(summary = "Complete admin registration", description = "Verify admin OTP and set password")
    public ResponseEntity<ApiResponse<Void>> completeAdminRegistration(
            @RequestBody @Valid AdminRegistrationCompleteRequest request) {
        log.info("Admin registration complete called for user: {}", request.getUserId());
        ApiResponse<Void> response = authService.completeAdminRegistration(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/resend-otp")
    @Operation(summary = "Resend OTP", description = "Resend verification OTP to the registered email")
    public ResponseEntity<ApiResponse<Void>> resendOtp(@RequestBody @Valid ResendOtpRequest request) {
        log.info("Resend OTP endpoint called for: {}", request.getEmail());
        ApiResponse<Void> response = authService.resendOtp(request);
        return ResponseEntity.ok(response);
    }



    @PostMapping("/login")
    @Operation(summary = "User login", description = "Login with email and password to get JWT tokens")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@RequestBody @Valid LoginRequest request) {
        log.info("Login endpoint called for: {}", request.getEmail());
        ApiResponse<LoginResponse> response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping({"/refresh", "/refresh-token"})
    @Operation(summary = "Refresh access token", description = "Get new access token using refresh token")
    public ResponseEntity<ApiResponse<LoginResponse>> refreshToken(@RequestBody @Valid RefreshTokenRequest request) {
        log.info("Refresh token endpoint called");
        ApiResponse<LoginResponse> response = authService.refreshAccessToken(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/forgot-password")
    @Operation(summary = "Forgot password", description = "Request password reset OTP")
    public ResponseEntity<ApiResponse<Void>> forgotPassword(@RequestBody @Valid ForgotPasswordRequest request) {
        log.info("Forgot password endpoint called for: {}", request.getEmail());
        ApiResponse<Void> response = authService.forgotPassword(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/reset-password")
    @Operation(summary = "Reset password", description = "Reset password using OTP")
    public ResponseEntity<ApiResponse<Void>> resetPassword(@RequestBody @Valid ResetPasswordRequest request) {
        log.info("Reset password endpoint called for: {}", request.getEmail());
        ApiResponse<Void> response = authService.resetPassword(request);
        return ResponseEntity.ok(response);
    }


}
