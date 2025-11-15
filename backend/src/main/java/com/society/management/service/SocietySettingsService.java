package com.society.management.service;

import com.society.management.dto.request.UpdateSocietyCodeRequest;
import com.society.management.dto.response.ApiResponse;
import com.society.management.dto.response.SocietySettingsDto;
import com.society.management.exception.ResourceNotFoundException;
import com.society.management.model.SocietySettings;
import com.society.management.repository.SocietySettingsRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SocietySettingsService {

    @Autowired
    private SocietySettingsRepository societySettingsRepository;

    // GET SOCIETY SETTINGS
    public ApiResponse<SocietySettingsDto> getSettings() {
        log.info("Fetching society settings");

        SocietySettings settings = societySettingsRepository.findFirstByOrderByCreatedAtAsc()
                .orElseThrow(() -> new ResourceNotFoundException("Society settings not found"));

        return ApiResponse.success(mapToDto(settings), "Settings fetched successfully");
    }

    // UPDATE SOCIETY CODE
    public ApiResponse<SocietySettingsDto> updateSocietyCode(String settingsId, UpdateSocietyCodeRequest request) {
        log.info("Updating society code");

        SocietySettings settings = societySettingsRepository.findById(settingsId)
                .orElseThrow(() -> new ResourceNotFoundException("Society settings not found"));

        settings.setSocietyCode(request.getNewSocietyCode());
        societySettingsRepository.save(settings);

        return ApiResponse.success(mapToDto(settings), "Society code updated successfully");
    }

    // UPDATE SETTINGS
    public ApiResponse<SocietySettingsDto> updateSettings(String settingsId, SocietySettingsDto dto) {
        log.info("Updating society settings");

        SocietySettings settings = societySettingsRepository.findById(settingsId)
                .orElseThrow(() -> new ResourceNotFoundException("Society settings not found"));

        if (dto.getSocietyName() != null) settings.setSocietyName(dto.getSocietyName());
        if (dto.getLogoUrl() != null) settings.setLogoUrl(dto.getLogoUrl());
        if (dto.getAddress() != null) settings.setAddress(dto.getAddress());
        if (dto.getAdminEmail() != null) settings.setAdminEmail(dto.getAdminEmail());
        if (dto.getAdminPhone() != null) settings.setAdminPhone(dto.getAdminPhone());
        if (dto.getDescription() != null) settings.setDescription(dto.getDescription());

        societySettingsRepository.save(settings);
        return ApiResponse.success(mapToDto(settings), "Settings updated successfully");
    }

    private SocietySettingsDto mapToDto(SocietySettings settings) {
        return SocietySettingsDto.builder()
                .id(settings.getId())
                .societyName(settings.getSocietyName())
                .societyCode(settings.getSocietyCode())
                .logoUrl(settings.getLogoUrl())
                .address(settings.getAddress())
                .adminEmail(settings.getAdminEmail())
                .adminPhone(settings.getAdminPhone())
                .description(settings.getDescription())
                .build();
    }
}
