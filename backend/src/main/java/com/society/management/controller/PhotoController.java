package com.society.management.controller;

import com.society.management.dto.request.PhotoRequest;
import com.society.management.dto.response.ApiResponse;
import com.society.management.dto.response.PhotoDto;
import com.society.management.service.PhotoService;
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

@RestController
@RequestMapping("/api/photos")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5000"})
@Slf4j
public class PhotoController {

    @Autowired
    private PhotoService photoService;

    private String getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (String) auth.getPrincipal();
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PhotoDto>> uploadPhoto(@Valid @RequestBody PhotoRequest request) {
        log.info("Upload photo");
        String userId = getCurrentUserId();
        return ResponseEntity.ok(photoService.uploadPhoto(userId, request));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<PhotoDto>>> getAllPhotos(Pageable pageable) {
        log.info("Get all photos");
        return ResponseEntity.ok(photoService.getAllPhotos(pageable));
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<ApiResponse<Page<PhotoDto>>> getByCategory(@PathVariable String category, Pageable pageable) {
        log.info("Get photos by category: {}", category);
        return ResponseEntity.ok(photoService.getPhotosByCategory(category, pageable));
    }

    @DeleteMapping("/{photoId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deletePhoto(@PathVariable String photoId) {
        log.info("Delete photo: {}", photoId);
        return ResponseEntity.ok(photoService.deletePhoto(photoId));
    }
}