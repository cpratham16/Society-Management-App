package com.society.management.controller;

import com.society.management.dto.request.ContactRequest;
import com.society.management.dto.response.ApiResponse;
import com.society.management.dto.response.ContactDto;
import com.society.management.service.ContactService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping({"/api/contacts", "/api/contact"})
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5000"})
@Slf4j
public class ContactController {

    @Autowired
    private ContactService contactService;

    @PostMapping
    public ResponseEntity<ApiResponse<Void>> submitContactForm(@Valid @RequestBody ContactRequest request) {
        log.info("Submit contact form");
        return ResponseEntity.ok(contactService.submitContactForm(request));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Page<ContactDto>>> getAllContacts(Pageable pageable) {
        log.info("Get all contacts - Admin");
        return ResponseEntity.ok(contactService.getAllContacts(pageable));
    }

    @DeleteMapping("/{contactId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteContact(@PathVariable String contactId) {
        log.info("Delete contact: {}", contactId);
        return ResponseEntity.ok(contactService.deleteContact(contactId));
    }
}