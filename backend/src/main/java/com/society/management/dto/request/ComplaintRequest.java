package com.society.management.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ComplaintRequest {
    @NotBlank
    private String subject;

    private String description;
    private String category;
    private List<String> attachmentUrls; // Cloudinary URLs

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateSocietyCodeRequest {
        @NotBlank
        private String newSocietyCode;
    }
}