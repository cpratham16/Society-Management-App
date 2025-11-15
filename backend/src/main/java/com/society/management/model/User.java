package com.society.management.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    private String id;

    @NotBlank
    private String name;

    @Email
    @Indexed(unique = true)
    private String email;

    @NotBlank
    private String password; // BCrypt hashed

    private String phone;
    private String profession;

    @Indexed
    private String societyCode;

    private Role role; // ADMIN, MEMBER, GUEST

    private boolean verified; // Email OTP verified

    // OTP Fields
    private String otp;
    private Instant otpExpiry;

    // Family Members - Embedded
    @Builder.Default
    private List<FamilyMember> familyMembers = new ArrayList<>();

    // Refresh Tokens for multiple device login
    @Builder.Default
    private List<String> refreshTokens = new ArrayList<>();

    // Profile Photo URL (from Cloudinary)
    private String profilePhotoUrl;

    // Metadata
    private Instant createdAt;
    private Instant updatedAt;

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", role=" + role +
                ", verified=" + verified +
                ", societyCode='" + societyCode + '\'' +
                '}';
    }
}