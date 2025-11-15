package com.society.management.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "society_settings")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SocietySettings {
    @Id
    private String id;

    private String societyName;
    private String societyCode; // Unique code for registration
    private String logoUrl; // Cloudinary URL
    private String address;
    private String adminEmail;
    private String adminPhone;
    private String description;

    @CreatedDate
    private LocalDateTime createdAt;  // ⭐ ADDED

    @LastModifiedDate
    private LocalDateTime updatedAt;  // ⭐ ADDED (optional but recommended)
}
