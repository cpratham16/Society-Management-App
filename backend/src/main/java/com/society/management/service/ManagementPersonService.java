package com.society.management.service;

import com.society.management.dto.request.ManagementPersonRequest;
import com.society.management.dto.response.ApiResponse;
import com.society.management.dto.response.ManagementPersonDto;
import com.society.management.exception.ResourceNotFoundException;
import com.society.management.model.ManagementPerson;
import com.society.management.repository.ManagementPersonRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@Slf4j
public class ManagementPersonService {

    @Autowired
    private ManagementPersonRepository managementPersonRepository;

    // CREATE MANAGEMENT PERSON
    public ApiResponse<ManagementPersonDto> createManagementPerson(ManagementPersonRequest request) {
        log.info("Adding management person: {}", request.getName());

        ManagementPerson person = ManagementPerson.builder()
                .name(request.getName())
                .designation(request.getDesignation())
                .photoUrl(request.getPhotoUrl())
                .phone(request.getPhone())
                .email(request.getEmail())
                .bio(request.getBio())
                .displayOrder(request.getDisplayOrder() != null ? request.getDisplayOrder() : 0)
                .createdAt(Instant.now())
                .build();

        managementPersonRepository.save(person);
        return ApiResponse.success(mapToDto(person), "Management person added successfully");
    }

    // GET ALL MANAGEMENT PEOPLE
    public ApiResponse<List<ManagementPersonDto>> getAllManagementPeople() {
        log.info("Fetching all management people");

        List<ManagementPerson> people = managementPersonRepository.findAllByOrderByDisplayOrderAsc();
        List<ManagementPersonDto> dtos = people.stream().map(this::mapToDto).toList();

        return ApiResponse.success(dtos, "Management people fetched successfully");
    }

    // UPDATE MANAGEMENT PERSON
    public ApiResponse<ManagementPersonDto> updateManagementPerson(String personId, ManagementPersonRequest request) {
        log.info("Updating management person: {}", personId);

        ManagementPerson person = managementPersonRepository.findById(personId)
                .orElseThrow(() -> new ResourceNotFoundException("Management person not found"));

        person.setName(request.getName());
        person.setDesignation(request.getDesignation());
        person.setPhotoUrl(request.getPhotoUrl());
        person.setPhone(request.getPhone());
        person.setEmail(request.getEmail());
        person.setBio(request.getBio());
        person.setDisplayOrder(request.getDisplayOrder());

        managementPersonRepository.save(person);
        return ApiResponse.success(mapToDto(person), "Management person updated successfully");
    }

    // DELETE MANAGEMENT PERSON
    public ApiResponse<Void> deleteManagementPerson(String personId) {
        log.info("Deleting management person: {}", personId);

        if (!managementPersonRepository.existsById(personId)) {
            throw new ResourceNotFoundException("Management person not found");
        }

        managementPersonRepository.deleteById(personId);
        return ApiResponse.success(null, "Management person deleted successfully");
    }

    private ManagementPersonDto mapToDto(ManagementPerson person) {
        return ManagementPersonDto.builder()
                .id(person.getId())
                .name(person.getName())
                .designation(person.getDesignation())
                .photoUrl(person.getPhotoUrl())
                .phone(person.getPhone())
                .email(person.getEmail())
                .bio(person.getBio())
                .displayOrder(person.getDisplayOrder())
                .createdAt(person.getCreatedAt())
                .build();
    }
}