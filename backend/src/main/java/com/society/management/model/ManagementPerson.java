package com.society.management.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.NotBlank;
import java.time.Instant;

@Document(collection = "management_people")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ManagementPerson {
    @Id
    private String id;

    @NotBlank
    private String name;

    private String designation; // President, Secretary, Treasurer, etc.
    private String photoUrl; // Cloudinary URL
    private String phone;
    private String email;
    private String bio;

    @Builder.Default
    private Integer displayOrder = 0; // For sorting in UI

    private Instant createdAt;
}