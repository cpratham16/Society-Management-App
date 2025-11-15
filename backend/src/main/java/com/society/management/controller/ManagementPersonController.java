package com.society.management.controller;

import com.society.management.dto.request.ManagementPersonRequest;
import com.society.management.dto.response.ApiResponse;
import com.society.management.dto.response.ManagementPersonDto;
import com.society.management.service.ManagementPersonService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/management")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5000"})
@Slf4j
public class ManagementPersonController {

    @Autowired
    private ManagementPersonService managementPersonService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ManagementPersonDto>> addManagementPerson(@Valid @RequestBody ManagementPersonRequest request) {
        log.info("Add management person");
        return ResponseEntity.ok(managementPersonService.createManagementPerson(request));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ManagementPersonDto>>> getAll() {
        log.info("Get all management people");
        return ResponseEntity.ok(managementPersonService.getAllManagementPeople());
    }

    @PutMapping("/{personId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ManagementPersonDto>> updateManagementPerson(@PathVariable String personId,
                                                                                   @Valid @RequestBody ManagementPersonRequest request) {
        log.info("Update management person: {}", personId);
        return ResponseEntity.ok(managementPersonService.updateManagementPerson(personId, request));
    }

    @DeleteMapping("/{personId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteManagementPerson(@PathVariable String personId) {
        log.info("Delete management person: {}", personId);
        return ResponseEntity.ok(managementPersonService.deleteManagementPerson(personId));
    }
}