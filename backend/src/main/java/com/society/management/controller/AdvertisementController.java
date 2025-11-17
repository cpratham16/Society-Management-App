package com.society.management.controller;

import com.society.management.dto.request.AdvertisementRequest;
import com.society.management.dto.response.AdvertisementDto;
import com.society.management.dto.response.ApiResponse;
import com.society.management.service.AdvertisementService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/advertisements")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5000"})
@Slf4j
public class AdvertisementController {

    @Autowired
    private AdvertisementService advertisementService;

    private String getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (String) auth.getPrincipal();
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('MEMBER', 'ADMIN')")
    public ResponseEntity<ApiResponse<AdvertisementDto>> createAdvertisement(@Valid @RequestBody AdvertisementRequest request) {
        log.info("Create advertisement");
        String userId = getCurrentUserId();
        return ResponseEntity.ok(advertisementService.createAdvertisement(userId, request));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<AdvertisementDto>>> getAllAdvertisements(Pageable pageable) {
        log.info("Get all advertisements");
        return ResponseEntity.ok(advertisementService.getAllAdvertisements(pageable));
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<ApiResponse<Page<AdvertisementDto>>> getByCategory(@PathVariable String category, Pageable pageable) {
        log.info("Get advertisements by category: {}", category);
        return ResponseEntity.ok(advertisementService.getAdvertisementsByCategory(category, pageable));
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAnyRole('MEMBER', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<AdvertisementDto>>> getUserAdvertisements(@PathVariable String userId) {
        log.info("Get user advertisements: {}", userId);
        return ResponseEntity.ok(advertisementService.getUserAdvertisements(userId));
    }

    @PutMapping("/{adId}")
    @PreAuthorize("hasAnyRole('MEMBER', 'ADMIN')")
    public ResponseEntity<ApiResponse<AdvertisementDto>> updateAdvertisement(@PathVariable String adId,
                                                                             @Valid @RequestBody AdvertisementRequest request) {
        log.info("Update advertisement: {}", adId);
        String userId = getCurrentUserId();
        return ResponseEntity.ok(advertisementService.updateAdvertisement(adId, request, userId));
    }

    @DeleteMapping("/{adId}")
    @PreAuthorize("hasAnyRole('MEMBER', 'ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteAdvertisement(@PathVariable String adId) {
        log.info("Delete advertisement: {}", adId);
        String userId = getCurrentUserId();
        return ResponseEntity.ok(advertisementService.deleteAdvertisement(adId, userId));
    }

    @PutMapping("/{adId}/toggle")
    @PreAuthorize("hasAnyRole('MEMBER', 'ADMIN')")
    public ResponseEntity<ApiResponse<AdvertisementDto>> toggleStatus(@PathVariable String adId) {
        log.info("Toggle advertisement status: {}", adId);
        String userId = getCurrentUserId();
        return ResponseEntity.ok(advertisementService.toggleAdvertisementStatus(adId, userId));
    }
}