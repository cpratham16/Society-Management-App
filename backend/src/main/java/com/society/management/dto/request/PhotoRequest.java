package com.society.management.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PhotoRequest {
    private String title;

    @NotBlank
    private String imageUrl; // Cloudinary URL

    private String category;
}