package com.society.management.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ManagementPersonRequest {
    @NotBlank
    private String name;

    private String designation;
    private String photoUrl; // Cloudinary URL
    private String phone;
    private String email;
    private String bio;
    private Integer displayOrder;
}
