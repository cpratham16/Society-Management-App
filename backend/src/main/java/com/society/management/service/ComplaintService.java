package com.society.management.service;

import com.society.management.dto.request.ComplaintRequest;
import com.society.management.dto.request.ComplaintStatusUpdateRequest;
import com.society.management.dto.response.ApiResponse;
import com.society.management.dto.response.ComplaintDto;
import com.society.management.exception.ResourceNotFoundException;
import com.society.management.model.Complaint;
import com.society.management.model.User;
import com.society.management.model.ComplaintStatus;
import com.society.management.repository.ComplaintRepository;
import com.society.management.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@Slf4j
public class ComplaintService {

    @Autowired
    private ComplaintRepository complaintRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    // CREATE COMPLAINT
    public ApiResponse<ComplaintDto> createComplaint(String userId, ComplaintRequest request) {
        log.info("Creating complaint for user: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Complaint complaint = Complaint.builder()
                .userId(userId)
                .subject(request.getSubject())
                .description(request.getDescription())
                .category(request.getCategory())
                .attachmentUrls(request.getAttachmentUrls())
                .status(ComplaintStatus.PENDING)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        complaintRepository.save(complaint);

        // Send notification email to admin
        User admin = userRepository.findByEmail("admin@society.com").orElse(null);
        if (admin != null) {
            emailService.sendComplaintNotificationEmail(admin.getEmail(), request.getSubject(), user.getName());
        }

        log.info("Complaint created: {}", complaint.getId());
        return ApiResponse.success(mapToDto(complaint), "Complaint filed successfully");
    }

    // GET USER'S COMPLAINTS
    public ApiResponse<List<ComplaintDto>> getUserComplaints(String userId) {
        log.info("Fetching complaints for user: {}", userId);

        List<Complaint> complaints = complaintRepository.findByUserIdOrderByCreatedAtDesc(userId);
        List<ComplaintDto> complaintDtos = complaints.stream().map(this::mapToDto).toList();

        return ApiResponse.success(complaintDtos, "Complaints fetched successfully");
    }

    // GET ALL COMPLAINTS (ADMIN)
    public ApiResponse<Page<ComplaintDto>> getAllComplaints(Pageable pageable) {
        log.info("Fetching all complaints");

        Page<Complaint> complaints = complaintRepository.findAll(pageable);
        Page<ComplaintDto> complaintDtos = complaints.map(this::mapToDto);

        return ApiResponse.success(complaintDtos, "Complaints fetched successfully");
    }

    // GET COMPLAINTS BY STATUS (ADMIN)
    public ApiResponse<Page<ComplaintDto>> getComplaintsByStatus(ComplaintStatus status, Pageable pageable) {
        log.info("Fetching complaints with status: {}", status);

        Page<Complaint> complaints = complaintRepository.findByStatus(status, pageable);
        Page<ComplaintDto> complaintDtos = complaints.map(this::mapToDto);

        return ApiResponse.success(complaintDtos, "Complaints fetched successfully");
    }

    // UPDATE COMPLAINT STATUS (ADMIN)
    public ApiResponse<ComplaintDto> updateComplaintStatus(String complaintId, ComplaintStatusUpdateRequest request) {
        log.info("Updating complaint status: {}", complaintId);

        Complaint complaint = complaintRepository.findById(complaintId)
                .orElseThrow(() -> new ResourceNotFoundException("Complaint not found"));

        complaint.setStatus(request.getStatus());
        complaint.setAdminResolution(request.getResolution());
        complaint.setUpdatedAt(Instant.now());

        complaintRepository.save(complaint);
        return ApiResponse.success(mapToDto(complaint), "Complaint status updated");
    }

    // DELETE COMPLAINT (ADMIN)
    public ApiResponse<Void> deleteComplaint(String complaintId) {
        log.info("Deleting complaint: {}", complaintId);

        if (!complaintRepository.existsById(complaintId)) {
            throw new ResourceNotFoundException("Complaint not found");
        }

        complaintRepository.deleteById(complaintId);
        return ApiResponse.success(null, "Complaint deleted successfully");
    }

    private ComplaintDto mapToDto(Complaint complaint) {
        return ComplaintDto.builder()
                .id(complaint.getId())
                .userId(complaint.getUserId())
                .subject(complaint.getSubject())
                .description(complaint.getDescription())
                .category(complaint.getCategory())
                .status(complaint.getStatus())
                .attachmentUrls(complaint.getAttachmentUrls())
                .adminResolution(complaint.getAdminResolution())
                .createdAt(complaint.getCreatedAt())
                .updatedAt(complaint.getUpdatedAt())
                .build();
    }
}