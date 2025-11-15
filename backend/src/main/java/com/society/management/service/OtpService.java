package com.society.management.service;

import com.society.management.model.User;
import com.society.management.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Optional;

@Service
@Slf4j
public class OtpService {

    @Autowired
    private UserRepository userRepository;

    // OTP Expiry Time (5 minutes in seconds)
    private static final long OTP_EXPIRY_SECONDS = 300;

    // Generate 6-digit OTP
    public String generateOtp() {
        SecureRandom random = new SecureRandom();
        int otp = random.nextInt(900000) + 100000; // 6-digit number
        return String.valueOf(otp);
    }

    // Create OTP with expiry and send email
    public void createAndSendOtp(String email, String name) {
        Optional<User> userOpt = userRepository.findByEmail(email);

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            String otp = generateOtp();

            // Set OTP and expiry
            user.setOtp(otp);
            user.setOtpExpiry(Instant.now().plusSeconds(OTP_EXPIRY_SECONDS));

            userRepository.save(user);

            // Send OTP email
            EmailService emailService = new EmailService();
            emailService.sendOtpEmail(email, otp);

            log.info("OTP created and sent for user: {}", email);
        }
    }

    // Validate OTP
    public boolean validateOtp(String email, String providedOtp) {
        Optional<User> userOpt = userRepository.findByEmail(email);

        if (userOpt.isEmpty()) {
            log.warn("User not found: {}", email);
            return false;
        }

        User user = userOpt.get();

        // Check if OTP matches
        if (!user.getOtp().equals(providedOtp)) {
            log.warn("OTP mismatch for user: {}", email);
            return false;
        }

        // Check if OTP has expired
        if (user.getOtpExpiry().isBefore(Instant.now())) {
            log.warn("OTP expired for user: {}", email);
            return false;
        }

        // OTP is valid
        user.setVerified(true);
        user.setOtp(null);
        user.setOtpExpiry(null);
        userRepository.save(user);

        log.info("OTP validated successfully for user: {}", email);
        return true;
    }
}