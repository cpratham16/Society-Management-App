package com.society.management.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ManagementPersonDto {
    private String id;
    private String name;
    private String designation;
    private String photoUrl;
    private String phone;
    private String email;
    private String bio;
    private Integer displayOrder;
    private Instant createdAt;
}