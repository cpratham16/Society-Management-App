package com.society.management.controller;

import com.society.management.dto.request.ComplaintRequest;
import com.society.management.dto.request.ComplaintStatusUpdateRequest;
import com.society.management.dto.response.ApiResponse;
import com.society.management.dto.response.ComplaintDto;
import com.society.management.model.ComplaintStatus;
import com.society.management.service.ComplaintService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/complaints")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5000"})
@Slf4j
public class ComplaintController {

    @Autowired
    private ComplaintService complaintService;

    private String getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (String) auth.getPrincipal();
    }

    @PostMapping
    @PreAuthorize("hasRole('MEMBER')")
    public ResponseEntity<ApiResponse<ComplaintDto>> createComplaint(@Valid @RequestBody ComplaintRequest request) {
        log.info("Create complaint");
        String userId = getCurrentUserId();
        return ResponseEntity.ok(complaintService.createComplaint(userId, request));
    }

    @GetMapping("/my-complaints")
    @PreAuthorize("hasRole('MEMBER')")
    public ResponseEntity<ApiResponse<List<ComplaintDto>>> getMyComplaints() {
        log.info("Get my complaints");
        String userId = getCurrentUserId();
        return ResponseEntity.ok(complaintService.getUserComplaints(userId));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Page<ComplaintDto>>> getAllComplaints(Pageable pageable) {
        log.info("Get all complaints - Admin");
        return ResponseEntity.ok(complaintService.getAllComplaints(pageable));
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Page<ComplaintDto>>> getByStatus(@PathVariable ComplaintStatus status, Pageable pageable) {
        log.info("Get complaints by status: {}", status);
        return ResponseEntity.ok(complaintService.getComplaintsByStatus(status, pageable));
    }

    @PutMapping("/{complaintId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ComplaintDto>> updateStatus(@PathVariable String complaintId,
                                                                  @Valid @RequestBody ComplaintStatusUpdateRequest request) {
        log.info("Update complaint status: {}", complaintId);
        return ResponseEntity.ok(complaintService.updateComplaintStatus(complaintId, request));
    }

    @DeleteMapping("/{complaintId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteComplaint(@PathVariable String complaintId) {
        log.info("Delete complaint: {}", complaintId);
        return ResponseEntity.ok(complaintService.deleteComplaint(complaintId));
    }
}