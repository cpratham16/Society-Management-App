package com.society.management.service;

import com.society.management.dto.request.*;
import com.society.management.dto.response.ApiResponse;
import com.society.management.dto.response.LoginResponse;
import com.society.management.exception.*;
import com.society.management.model.RefreshToken;
import com.society.management.model.User;
import com.society.management.model.Role;
import com.society.management.repository.RefreshTokenRepository;
import com.society.management.repository.SocietySettingsRepository;
import com.society.management.repository.UserRepository;
import com.society.management.security.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@Slf4j
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private SocietySettingsRepository societySettingsRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    @Autowired
    private OtpService otpService;

    // ======================== REGISTRATION ========================
    public ApiResponse<Void> register(RegisterRequest request) {
        log.info("Registering new user: {}", request.getEmail());

        // Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered");
        }

        // Validate society code
        var societySettings = societySettingsRepository.findBySocietyCode(request.getSocietyCode())
                .orElseThrow(() -> new SocietyCodeMismatchException("Invalid society code"));

        // Create new user
        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .phone(request.getPhone())
                .profession(request.getProfession())
                .societyCode(request.getSocietyCode())
                .role(Role.MEMBER)
                .verified(false)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        // Generate and save OTP
        String otp = otpService.generateOtp();
        user.setOtp(otp);
        user.setOtpExpiry(Instant.now().plusSeconds(300)); // 5 minutes

        userRepository.save(user);

        // Send OTP email
        emailService.sendOtpEmail(request.getEmail(), otp);

        log.info("User registered successfully: {}", request.getEmail());
        return ApiResponse.success(null, "Registration successful. Check your email for OTP.");
    }

    // ======================== OTP VERIFICATION ========================
    public ApiResponse<Void> verifyOtp(VerifyOtpRequest request) {
        log.info("Verifying OTP for: {}", request.getEmail());

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        // Validate OTP
        if (!request.getOtp().equals(user.getOtp())) {
            throw new InvalidOtpException("Invalid OTP");
        }

        // Check OTP expiry
        if (user.getOtpExpiry().isBefore(Instant.now())) {
            throw new InvalidOtpException("OTP has expired");
        }

        // Mark as verified
        user.setVerified(true);
        user.setOtp(null);
        user.setOtpExpiry(null);
        user.setUpdatedAt(Instant.now());

        userRepository.save(user);

        // Send welcome email
        emailService.sendWelcomeEmail(user.getEmail(), user.getName());

        log.info("OTP verified successfully for: {}", request.getEmail());
        return ApiResponse.success(null, "Email verified successfully");
    }

    // ======================== LOGIN ========================
    public ApiResponse<LoginResponse> login(LoginRequest request) {
        log.info("Login attempt for: {}", request.getEmail());

        User user = userRepository.findVerifiedByEmail(request.getEmail())
                .orElseThrow(() -> new UserNotFoundException("User not found or not verified"));

        // Validate password
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        // Generate tokens
        String accessToken = jwtUtil.generateAccessToken(user.getId(), user.getEmail(), user.getRole().name());
        String refreshToken = jwtUtil.generateRefreshToken(user.getId());

        // Save refresh token
        RefreshToken refToken = RefreshToken.builder()
                .token(refreshToken)
                .userId(user.getId())
                .expiryDate(Instant.now().plusSeconds(86400)) // 24 hours
                .createdAt(Instant.now())
                .build();

        refreshTokenRepository.save(refToken);

        // Add to user's refresh token list
        user.getRefreshTokens().add(refreshToken);
        userRepository.save(user);

        log.info("User logged in successfully: {}", request.getEmail());

        return ApiResponse.success(
                new LoginResponse(accessToken, refreshToken, user.getId(),
                        user.getEmail(), user.getName(), user.getRole()),
                "Login successful"
        );
    }

    // ======================== REFRESH TOKEN ========================
    public ApiResponse<LoginResponse> refreshAccessToken(RefreshTokenRequest request) {
        log.info("Refreshing access token");

        RefreshToken refreshToken = refreshTokenRepository.findByToken(request.getRefreshToken())
                .orElseThrow(() -> new InvalidTokenException("Invalid refresh token"));

        // Check expiry
        if (refreshToken.getExpiryDate().isBefore(Instant.now())) {
            refreshTokenRepository.delete(refreshToken);
            throw new InvalidTokenException("Refresh token has expired");
        }

        User user = userRepository.findById(refreshToken.getUserId())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        // Generate new access token
        String newAccessToken = jwtUtil.generateAccessToken(user.getId(), user.getEmail(), user.getRole().name());

        log.info("Access token refreshed for user: {}", user.getEmail());

        return ApiResponse.success(
                new LoginResponse(newAccessToken, request.getRefreshToken(), user.getId(),
                        user.getEmail(), user.getName(), user.getRole()),
                "Access token refreshed"
        );
    }

    // ======================== FORGOT PASSWORD ========================
    public ApiResponse<Void> forgotPassword(ForgotPasswordRequest request) {
        log.info("Forgot password request for: {}", request.getEmail());

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        // Generate and set OTP for password reset
        String otp = otpService.generateOtp();
        user.setOtp(otp);
        user.setOtpExpiry(Instant.now().plusSeconds(300)); // 5 minutes

        userRepository.save(user);

        // Send password reset OTP email
        emailService.sendPasswordResetEmail(user.getEmail(), otp);

        log.info("Password reset OTP sent to: {}", request.getEmail());
        return ApiResponse.success(null, "Password reset OTP sent to your email");
    }

    // ======================== RESET PASSWORD ========================
    public ApiResponse<Void> resetPassword(ResetPasswordRequest request) {
        log.info("Reset password for: {}", request.getEmail());

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        // Validate OTP
        if (!request.getOtp().equals(user.getOtp())) {
            throw new InvalidOtpException("Invalid OTP");
        }

        // Check OTP expiry
        if (user.getOtpExpiry().isBefore(Instant.now())) {
            throw new InvalidOtpException("OTP has expired");
        }

        // Update password
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setOtp(null);
        user.setOtpExpiry(null);
        user.setUpdatedAt(Instant.now());

        userRepository.save(user);

        log.info("Password reset successfully for: {}", request.getEmail());
        return ApiResponse.success(null, "Password reset successfully");
    }

    // ======================== LOGOUT ========================
    public ApiResponse<Void> logout(String userId, String refreshToken) {
        log.info("Logout for user: {}", userId);

        // Delete refresh token
        refreshTokenRepository.deleteByUserId(userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        // Clear refresh tokens from user
        user.getRefreshTokens().clear();
        userRepository.save(user);

        log.info("User logged out: {}", userId);
        return ApiResponse.success(null, "Logged out successfully");
    }
}