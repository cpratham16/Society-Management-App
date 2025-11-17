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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "events")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Event {
    @Id
    private String id;

    @NotBlank
    private String title;

    private String description;
    private String location;

    @Indexed
    private LocalDateTime eventDateTime; // Event date and time

    @Builder.Default
    private List<String> imageUrls = new ArrayList<>(); // Cloudinary URLs

    private String createdByUserId; // Admin who created

    private Instant createdAt;
    private Instant updatedAt;
}