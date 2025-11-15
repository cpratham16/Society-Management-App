package com.society.management.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

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