package com.society.management.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventDto {
    private String id;
    private String title;
    private String description;
    private String location;
    private LocalDateTime eventDateTime;
    private List<String> imageUrls;
    private String createdByUserId;
    private Instant createdAt;
    private Instant updatedAt;
}