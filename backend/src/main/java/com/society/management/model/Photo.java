package com.society.management.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.NotBlank;
import java.time.Instant;

@Document(collection = "photos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Photo {
    @Id
    private String id;

    private String title;

    @NotBlank
    private String imageUrl; // Cloudinary secure URL

    private String category; // Event, Gallery, Facility, etc.
    private String uploadedByUserId; // Admin who uploaded

    private Instant createdAt;
}