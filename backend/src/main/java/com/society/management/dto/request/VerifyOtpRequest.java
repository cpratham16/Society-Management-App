package com.society.management.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VerifyOtpRequest {
    @Email
    @NotBlank
    private String email;

    @NotBlank(message = "OTP is required")
    private String otp;
}