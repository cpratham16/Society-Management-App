package com.society.management.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardStatsResponse {
    private Long totalMembers;
    private Long totalEvents;
    private Long totalAdvertisements;
    private Long pendingComplaints;
    private Long resolvedComplaints;
    private Long totalComplaints;
    private Long totalContacts;
}