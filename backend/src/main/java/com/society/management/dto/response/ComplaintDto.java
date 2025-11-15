package com.society.management.dto.response;

import com.society.management.model.ComplaintStatus;
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
public class ComplaintDto {
    private String id;
    private String userId;
    private String subject;
    private String description;
    private String category;
    private ComplaintStatus status;
    private List<String> attachmentUrls;
    private String adminResolution;
    private Instant createdAt;
    private Instant updatedAt;
}