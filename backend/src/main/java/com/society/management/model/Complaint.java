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

@Document(collection = "complaints")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Complaint {
    @Id
    private String id;

    @Indexed
    private String userId; // Member who filed

    @NotBlank
    private String subject;

    private String description;
    private String category; // Maintenance, Noise, Parking, etc.

    private ComplaintStatus status; // PENDING, RESOLVED, REJECTED

    @Builder.Default
    private List<String> attachmentUrls = new ArrayList<>(); // Cloudinary URLs

    private String adminResolution; // Admin's response

    private Instant createdAt;
    private Instant updatedAt;
}