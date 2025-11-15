package com.society.management.controller;

import com.society.management.dto.request.UpdateSocietyCodeRequest;
import com.society.management.dto.response.ApiResponse;
import com.society.management.dto.response.DashboardStatsResponse;
import com.society.management.dto.response.SocietySettingsDto;
import com.society.management.model.ComplaintStatus;
import com.society.management.model.Role;
import com.society.management.repository.*;
import com.society.management.service.SocietySettingsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5000"})
@Slf4j
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired
    private SocietySettingsService societySettingsService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private AdvertisementRepository advertisementRepository;

    @Autowired
    private ComplaintRepository complaintRepository;

    @Autowired
    private ContactRepository contactRepository;

    @Autowired
    private SocietySettingsRepository societySettingsRepository;

    @GetMapping("/settings")
    public ResponseEntity<ApiResponse<SocietySettingsDto>> getSettings() {
        log.info("Get society settings");
        return ResponseEntity.ok(societySettingsService.getSettings());
    }

    @PutMapping("/settings/{settingsId}")
    public ResponseEntity<ApiResponse<SocietySettingsDto>> updateSettings(@PathVariable String settingsId,
                                                                          @RequestBody SocietySettingsDto dto) {
        log.info("Update settings");
        return ResponseEntity.ok(societySettingsService.updateSettings(settingsId, dto));
    }

    @PutMapping("/settings/{settingsId}/society-code")
    public ResponseEntity<ApiResponse<SocietySettingsDto>> updateSocietyCode(@PathVariable String settingsId,
                                                                             @Valid @RequestBody UpdateSocietyCodeRequest request) {
        log.info("Update society code");
        return ResponseEntity.ok(societySettingsService.updateSocietyCode(settingsId, request));
    }

    @GetMapping("/dashboard/stats")
    public ResponseEntity<ApiResponse<DashboardStatsResponse>> getDashboardStats() {
        log.info("Get dashboard stats");

        Long totalMembers = userRepository.countByRole(Role.MEMBER);
        Long totalEvents = eventRepository.count();
        Long totalAdvertisements = advertisementRepository.countByActive(true);
        Long pendingComplaints = complaintRepository.countByStatus(ComplaintStatus.PENDING);
        Long resolvedComplaints = complaintRepository.countByStatus(ComplaintStatus.RESOLVED);
        Long totalComplaints = (long) complaintRepository.findAll().size();

        DashboardStatsResponse stats = DashboardStatsResponse.builder()
                .totalMembers(totalMembers != null ? totalMembers : 0L)
                .totalEvents(totalEvents != null ? totalEvents : 0L)
                .totalAdvertisements(totalAdvertisements != null ? totalAdvertisements : 0L)
                .pendingComplaints(pendingComplaints != null ? pendingComplaints : 0L)
                .resolvedComplaints(resolvedComplaints != null ? resolvedComplaints : 0L)
                .totalComplaints(totalComplaints)
                .totalContacts(contactRepository.count())
                .build();

        return ResponseEntity.ok(ApiResponse.success(stats, "Dashboard stats fetched"));
    }
}