package com.society.management.service;

import com.society.management.dto.request.AdvertisementRequest;
import com.society.management.dto.response.AdvertisementDto;
import com.society.management.dto.response.ApiResponse;
import com.society.management.exception.ResourceNotFoundException;
import com.society.management.exception.UnauthorizedException;
import com.society.management.model.Advertisement;
import com.society.management.repository.AdvertisementRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@Slf4j
public class AdvertisementService {

    @Autowired
    private AdvertisementRepository advertisementRepository;

    // ======================== CREATE ADVERTISEMENT ========================
    public ApiResponse<AdvertisementDto> createAdvertisement(String userId, AdvertisementRequest request) {
        log.info("Creating advertisement for user: {}", userId);

        Advertisement advertisement = Advertisement.builder()
                .userId(userId)
                .title(request.getTitle())
                .description(request.getDescription())
                .category(request.getCategory())
                .contactInfo(request.getContactInfo())
                .imageUrls(request.getImageUrls())
                .active(true)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        advertisementRepository.save(advertisement);

        log.info("Advertisement created: {}", advertisement.getId());
        return ApiResponse.success(mapToDto(advertisement), "Advertisement created successfully");
    }

    // ======================== GET ALL ACTIVE ADVERTISEMENTS ========================
    public ApiResponse<Page<AdvertisementDto>> getAllAdvertisements(Pageable pageable) {
        log.info("Fetching all active advertisements");

        Page<Advertisement> ads = advertisementRepository.findByActiveTrue(pageable);
        Page<AdvertisementDto> adDtos = ads.map(this::mapToDto);

        return ApiResponse.success(adDtos, "Advertisements fetched successfully");
    }

    // ======================== GET ADVERTISEMENTS BY CATEGORY ========================
    public ApiResponse<Page<AdvertisementDto>> getAdvertisementsByCategory(String category, Pageable pageable) {
        log.info("Fetching advertisements for category: {}", category);

        Page<Advertisement> ads = advertisementRepository.findByActiveTrueAndCategory(category, pageable);
        Page<AdvertisementDto> adDtos = ads.map(this::mapToDto);

        return ApiResponse.success(adDtos, "Advertisements fetched successfully");
    }

    // ======================== GET USER'S ADVERTISEMENTS ========================
    public ApiResponse<java.util.List<AdvertisementDto>> getUserAdvertisements(String userId) {
        log.info("Fetching advertisements for user: {}", userId);

        var ads = advertisementRepository.findByUserIdOrderByCreatedAtDesc(userId);
        var adDtos = ads.stream().map(this::mapToDto).toList();

        return ApiResponse.success(adDtos, "User advertisements fetched successfully");
    }

    // ======================== UPDATE ADVERTISEMENT ========================
    public ApiResponse<AdvertisementDto> updateAdvertisement(String adId, AdvertisementRequest request, String userId) {
        log.info("Updating advertisement: {}", adId);

        Advertisement ad = advertisementRepository.findById(adId)
                .orElseThrow(() -> new ResourceNotFoundException("Advertisement not found"));

        if (!ad.getUserId().equals(userId)) {
            throw new UnauthorizedException("You can only update your own advertisements");
        }

        ad.setTitle(request.getTitle());
        ad.setDescription(request.getDescription());
        ad.setCategory(request.getCategory());
        ad.setContactInfo(request.getContactInfo());
        ad.setImageUrls(request.getImageUrls());
        ad.setUpdatedAt(Instant.now());

        advertisementRepository.save(ad);

        return ApiResponse.success(mapToDto(ad), "Advertisement updated successfully");
    }

    // ======================== DELETE ADVERTISEMENT ========================
    public ApiResponse<Void> deleteAdvertisement(String adId, String userId) {
        log.info("Deleting advertisement: {}", adId);

        Advertisement ad = advertisementRepository.findById(adId)
                .orElseThrow(() -> new ResourceNotFoundException("Advertisement not found"));

        if (!ad.getUserId().equals(userId)) {
            throw new UnauthorizedException("You can only delete your own advertisements");
        }

        advertisementRepository.deleteById(adId);

        return ApiResponse.success(null, "Advertisement deleted successfully");
    }

    // ======================== TOGGLE ADVERTISEMENT STATUS ========================
    public ApiResponse<AdvertisementDto> toggleAdvertisementStatus(String adId, String userId) {
        log.info("Toggling status for advertisement: {}", adId);

        Advertisement ad = advertisementRepository.findById(adId)
                .orElseThrow(() -> new ResourceNotFoundException("Advertisement not found"));

        if (!ad.getUserId().equals(userId)) {
            throw new UnauthorizedException("You can only update your own advertisements");
        }

        ad.setActive(!ad.isActive());
        ad.setUpdatedAt(Instant.now());

        advertisementRepository.save(ad);

        return ApiResponse.success(mapToDto(ad), "Advertisement status updated");
    }

    // ======================== HELPER METHOD ========================
    private AdvertisementDto mapToDto(Advertisement ad) {
        return AdvertisementDto.builder()
                .id(ad.getId())
                .userId(ad.getUserId())
                .title(ad.getTitle())
                .description(ad.getDescription())
                .category(ad.getCategory())
                .contactInfo(ad.getContactInfo())
                .imageUrls(ad.getImageUrls())
                .active(ad.isActive())
                .createdAt(ad.getCreatedAt())
                .updatedAt(ad.getUpdatedAt())
                .build();
    }
}