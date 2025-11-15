package com.society.management.service;

import com.society.management.dto.request.PhotoRequest;
import com.society.management.dto.response.ApiResponse;
import com.society.management.dto.response.PhotoDto;
import com.society.management.exception.ResourceNotFoundException;
import com.society.management.model.Photo;
import com.society.management.repository.PhotoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@Slf4j
public class PhotoService {

    @Autowired
    private PhotoRepository photoRepository;

    // UPLOAD PHOTO
    public ApiResponse<PhotoDto> uploadPhoto(String uploadedByUserId, PhotoRequest request) {
        log.info("Uploading photo: {}", request.getTitle());

        Photo photo = Photo.builder()
                .title(request.getTitle())
                .imageUrl(request.getImageUrl())
                .category(request.getCategory())
                .uploadedByUserId(uploadedByUserId)
                .createdAt(Instant.now())
                .build();

        photoRepository.save(photo);
        return ApiResponse.success(mapToDto(photo), "Photo uploaded successfully");
    }

    // GET ALL PHOTOS
    public ApiResponse<Page<PhotoDto>> getAllPhotos(Pageable pageable) {
        log.info("Fetching all photos");

        Page<Photo> photos = photoRepository.findAll(pageable);
        Page<PhotoDto> photoDtos = photos.map(this::mapToDto);

        return ApiResponse.success(photoDtos, "Photos fetched successfully");
    }

    // GET PHOTOS BY CATEGORY
    public ApiResponse<Page<PhotoDto>> getPhotosByCategory(String category, Pageable pageable) {
        log.info("Fetching photos for category: {}", category);

        Page<Photo> photos = photoRepository.findByCategory(category, pageable);
        Page<PhotoDto> photoDtos = photos.map(this::mapToDto);

        return ApiResponse.success(photoDtos, "Photos fetched successfully");
    }

    // DELETE PHOTO
    public ApiResponse<Void> deletePhoto(String photoId) {
        log.info("Deleting photo: {}", photoId);

        if (!photoRepository.existsById(photoId)) {
            throw new ResourceNotFoundException("Photo not found");
        }

        photoRepository.deleteById(photoId);
        return ApiResponse.success(null, "Photo deleted successfully");
    }

    private PhotoDto mapToDto(Photo photo) {
        return PhotoDto.builder()
                .id(photo.getId())
                .title(photo.getTitle())
                .imageUrl(photo.getImageUrl())
                .category(photo.getCategory())
                .uploadedByUserId(photo.getUploadedByUserId())
                .createdAt(photo.getCreatedAt())
                .build();
    }
}