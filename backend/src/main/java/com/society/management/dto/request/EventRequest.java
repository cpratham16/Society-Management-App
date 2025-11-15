package com.society.management.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventRequest {
    @NotBlank
    private String title;

    private String description;
    private String location;

    @NotBlank
    private LocalDateTime eventDateTime;

    private List<String> imageUrls; // Cloudinary URLs
}