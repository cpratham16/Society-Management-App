package com.society.management.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;

import jakarta.validation.constraints.NotBlank;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "advertisements")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Advertisement {
    @Id
    private String id;

    @Indexed
    private String userId; // Member who posted

    @NotBlank
    private String title;

    private String description;
    private String category; // Service, Product, etc.
    private String contactInfo; // Phone/Email

    @Builder.Default
    private List<String> imageUrls = new ArrayList<>(); // Cloudinary URLs

    @Builder.Default
    private boolean active = true;

    private Instant createdAt;
    private Instant updatedAt;
}