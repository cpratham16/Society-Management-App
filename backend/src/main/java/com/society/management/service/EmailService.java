package com.society.management.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
@Slf4j
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    // Send OTP Email
    public void sendOtpEmail(String toEmail, String otp) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("Your OTP for Society Management Registration");

            String htmlContent = "<html>" +
                    "<body style='font-family: Arial, sans-serif;'>" +
                    "<h2>Email Verification</h2>" +
                    "<p>Your One-Time Password (OTP) for registration is:</p>" +
                    "<h1 style='color: #4CAF50; letter-spacing: 5px;'>" + otp + "</h1>" +
                    "<p>This OTP will expire in 5 minutes.</p>" +
                    "<p>If you didn't request this, please ignore this email.</p>" +
                    "<hr>" +
                    "<p><small>Society Management System</small></p>" +
                    "</body>" +
                    "</html>";

            helper.setText(htmlContent, true);
            mailSender.send(message);

            log.info("OTP email sent successfully to: {}", toEmail);
        } catch (MessagingException e) {
            log.error("Failed to send OTP email to {}: {}", toEmail, e.getMessage());
            throw new RuntimeException("Failed to send email", e);
        }
    }

    // Send Welcome Email
    public void sendWelcomeEmail(String toEmail, String name) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("Welcome to Society Management System");

            String htmlContent = "<html>" +
                    "<body style='font-family: Arial, sans-serif;'>" +
                    "<h2>Welcome " + name + "!</h2>" +
                    "<p>Your account has been successfully created.</p>" +
                    "<p>You can now log in to the Society Management System and start managing your profile.</p>" +
                    "<p><strong>Features available:</strong></p>" +
                    "<ul>" +
                    "<li>Manage your profile and family members</li>" +
                    "<li>Post advertisements for your business</li>" +
                    "<li>File complaints to society management</li>" +
                    "<li>View events and gallery</li>" +
                    "</ul>" +
                    "<hr>" +
                    "<p><small>Society Management System</small></p>" +
                    "</body>" +
                    "</html>";

            helper.setText(htmlContent, true);
            mailSender.send(message);

            log.info("Welcome email sent to: {}", toEmail);
        } catch (MessagingException e) {
            log.error("Failed to send welcome email: {}", e.getMessage());
        }
    }

    // Send Password Reset OTP Email
    public void sendPasswordResetEmail(String toEmail, String otp) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("Password Reset - OTP");

            String htmlContent = "<html>" +
                    "<body style='font-family: Arial, sans-serif;'>" +
                    "<h2>Password Reset Request</h2>" +
                    "<p>Your One-Time Password (OTP) to reset your password is:</p>" +
                    "<h1 style='color: #2196F3; letter-spacing: 5px;'>" + otp + "</h1>" +
                    "<p>This OTP will expire in 5 minutes.</p>" +
                    "<p>If you didn't request a password reset, please ignore this email.</p>" +
                    "<hr>" +
                    "<p><small>Society Management System</small></p>" +
                    "</body>" +
                    "</html>";

            helper.setText(htmlContent, true);
            mailSender.send(message);

            log.info("Password reset email sent to: {}", toEmail);
        } catch (MessagingException e) {
            log.error("Failed to send password reset email: {}", e.getMessage());
        }
    }

    // Send Complaint Notification to Admin
    public void sendComplaintNotificationEmail(String adminEmail, String complaintSubject, String memberName) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(adminEmail);
            helper.setSubject("New Complaint Filed - " + complaintSubject);

            String htmlContent = "<html>" +
                    "<body style='font-family: Arial, sans-serif;'>" +
                    "<h2>New Complaint Alert</h2>" +
                    "<p>A new complaint has been filed by <strong>" + memberName + "</strong></p>" +
                    "<p><strong>Subject:</strong> " + complaintSubject + "</p>" +
                    "<p>Please log in to the admin panel to view and respond to this complaint.</p>" +
                    "<hr>" +
                    "<p><small>Society Management System</small></p>" +
                    "</body>" +
                    "</html>";

            helper.setText(htmlContent, true);
            mailSender.send(message);

            log.info("Complaint notification sent to admin: {}", adminEmail);
        } catch (MessagingException e) {
            log.error("Failed to send complaint notification: {}", e.getMessage());
        }
    }

    // Send Contact Form Response Email
    public void sendContactFormNotificationEmail(String adminEmail, String contactName, String contactEmail) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(adminEmail);
            helper.setSubject("New Contact Form Submission from " + contactName);

            String htmlContent = "<html>" +
                    "<body style='font-family: Arial, sans-serif;'>" +
                    "<h2>New Contact Form Submission</h2>" +
                    "<p><strong>Name:</strong> " + contactName + "</p>" +
                    "<p><strong>Email:</strong> " + contactEmail + "</p>" +
                    "<p>Please log in to view the complete message.</p>" +
                    "<hr>" +
                    "<p><small>Society Management System</small></p>" +
                    "</body>" +
                    "</html>";

            helper.setText(htmlContent, true);
            mailSender.send(message);

            log.info("Contact form notification sent to admin: {}", adminEmail);
        } catch (MessagingException e) {
            log.error("Failed to send contact notification: {}", e.getMessage());
        }
    }
}