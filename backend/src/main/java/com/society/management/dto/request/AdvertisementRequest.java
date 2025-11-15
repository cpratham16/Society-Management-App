package com.society.management.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdvertisementRequest {
    @NotBlank
    private String title;

    private String description;
    private String category;
    private String contactInfo;
    private List<String> imageUrls; // Cloudinary URLs
}