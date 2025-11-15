package com.society.management.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdvertisementDto {
    private String id;
    private String userId;
    private String title;
    private String description;
    private String category;
    private String contactInfo;
    private List<String> imageUrls;
    private boolean active;
    private Instant createdAt;
    private Instant updatedAt;
}