package com.society.management.dto.request;

import com.society.management.model.ComplaintStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ComplaintStatusUpdateRequest {
    private ComplaintStatus status;
    private String resolution;
}