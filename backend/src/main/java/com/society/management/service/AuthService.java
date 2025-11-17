package com.society.management.service;

import com.society.management.dto.request.*;
import com.society.management.dto.response.AdminRegistrationInitResponse;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.UUID;

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

    @Value("${app.society.default-code:DEMO123}")
    private String defaultSocietyCode;

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
                .houseNo(StringUtils.hasText(request.getHouseNo()) ? request.getHouseNo() : "")
                .familyName(request.getName())
                .totalMembers(1)
                .professionDescription(request.getProfession())
                .publicUrl(generatePublicUrl(request.getName()))
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

    // ======================== RESEND OTP ========================
    public ApiResponse<Void> resendOtp(ResendOtpRequest request) {
        log.info("Resending OTP for: {}", request.getEmail());

        // Find user by email
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        // Check if already verified
        if (user.isVerified()) {
            throw new RuntimeException("User is already verified");
        }

        // Check if OTP was recently sent (rate limiting - optional)
        if (user.getOtpExpiry() != null && user.getOtpExpiry().isAfter(Instant.now())) {
            long secondsRemaining = user.getOtpExpiry().getEpochSecond() - Instant.now().getEpochSecond();
            if (secondsRemaining > 240) {  // If less than 1 minute has passed
                throw new RuntimeException("Please wait " + secondsRemaining + " seconds before requesting a new OTP");
            }
        }

        // Generate and set new OTP
        String otp = otpService.generateOtp();
        user.setOtp(otp);
        user.setOtpExpiry(Instant.now().plusSeconds(300)); // 5 minutes
        user.setUpdatedAt(Instant.now());

        userRepository.save(user);

        // Send OTP email
        emailService.sendOtpEmail(request.getEmail(), otp);

        log.info("OTP resent successfully to: {}", request.getEmail());
        return ApiResponse.success(null, "OTP resent successfully. Check your email.");
    }

    // ======================== ADMIN REGISTRATION ========================
    public ApiResponse<AdminRegistrationInitResponse> initiateAdminRegistration(AdminRegistrationInitiateRequest request) {
        log.info("Initiating admin registration for: {}", request.getEmail());

        if (!request.getAdminCode().equalsIgnoreCase(defaultSocietyCode)) {
            throw new InvalidTokenException("Invalid admin code");
        }

        User user = userRepository.findByEmail(request.getEmail()).orElse(
                User.builder()
                        .email(request.getEmail())
                        .role(Role.ADMIN)
                        .societyCode(defaultSocietyCode)
                        .verified(false)
                        .createdAt(Instant.now())
                        .build()
        );

        if (user.isVerified() && user.getRole() == Role.ADMIN) {
            throw new RuntimeException("An admin is already registered with this email");
        }

        user.setName(request.getName());
        user.setRole(Role.ADMIN);
        user.setSocietyCode(defaultSocietyCode);
        user.setUpdatedAt(Instant.now());

        if (!StringUtils.hasText(user.getPassword())) {
            user.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
        }

        String otp = otpService.generateOtp();
        user.setOtp(otp);
        user.setOtpExpiry(Instant.now().plusSeconds(300));

        userRepository.save(user);
        emailService.sendOtpEmail(user.getEmail(), otp);

        AdminRegistrationInitResponse response = AdminRegistrationInitResponse.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .build();

        return ApiResponse.success(response, "OTP sent to admin email");
    }

    public ApiResponse<Void> completeAdminRegistration(AdminRegistrationCompleteRequest request) {
        log.info("Completing admin registration for: {}", request.getUserId());

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new UserNotFoundException("Admin registration not found"));

        if (user.getRole() != Role.ADMIN) {
            throw new RuntimeException("User is not authorized for admin registration");
        }

        if (!request.getOtp().equals(user.getOtp())) {
            throw new InvalidOtpException("Invalid OTP");
        }

        if (user.getOtpExpiry() == null || user.getOtpExpiry().isBefore(Instant.now())) {
            throw new InvalidOtpException("OTP has expired");
        }

        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setOtp(null);
        user.setOtpExpiry(null);
        user.setVerified(true);
        user.setUpdatedAt(Instant.now());

        if (StringUtils.hasText(request.getProfilePhoto())) {
            user.setProfilePhotoUrl(request.getProfilePhoto());
        }

        userRepository.save(user);
        emailService.sendWelcomeEmail(user.getEmail(), user.getName());

        return ApiResponse.success(null, "Admin registered successfully");
    }

    private String generatePublicUrl(String seedValue) {
        String base = StringUtils.hasText(seedValue) ? seedValue : "member";
        String slug = base.trim().toLowerCase().replaceAll("[^a-z0-9]+", "-");
        if (!StringUtils.hasText(slug)) {
            slug = "member";
        }
        String random = UUID.randomUUID().toString().substring(0, 8);
        return slug + "-" + random;
    }
}