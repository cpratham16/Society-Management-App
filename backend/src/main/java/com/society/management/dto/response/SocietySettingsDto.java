package com.society.management.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SocietySettingsDto {
    private String id;
    private String societyName;
    private String societyCode;
    private String logoUrl;
    private String address;
    private String adminEmail;
    private String adminPhone;
    private String description;
}