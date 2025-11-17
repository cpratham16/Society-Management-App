package com.society.management.controller;

import com.society.management.dto.request.EventRequest;
import com.society.management.dto.response.ApiResponse;
import com.society.management.dto.response.EventDto;
import com.society.management.service.EventService;
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
@RequestMapping("/api/events")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5000"})
@Slf4j
public class EventController {

    @Autowired
    private EventService eventService;

    private String getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (String) auth.getPrincipal();
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<EventDto>> createEvent(@Valid @RequestBody EventRequest request) {
        log.info("Create event");
        String adminId = getCurrentUserId();
        return ResponseEntity.ok(eventService.createEvent(adminId, request));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<EventDto>>> getAllEvents(Pageable pageable) {
        log.info("Get all events");
        return ResponseEntity.ok(eventService.getAllEvents(pageable));
    }

    @GetMapping("/upcoming")
    public ResponseEntity<ApiResponse<List<EventDto>>> getUpcomingEvents() {
        log.info("Get upcoming events");
        return ResponseEntity.ok(eventService.getUpcomingEvents());
    }

    @GetMapping("/{eventId}")
    public ResponseEntity<ApiResponse<EventDto>> getEventById(@PathVariable String eventId) {
        log.info("Get event: {}", eventId);
        return ResponseEntity.ok(eventService.getEventById(eventId));
    }

    @PutMapping("/{eventId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<EventDto>> updateEvent(@PathVariable String eventId,
                                                             @Valid @RequestBody EventRequest request) {
        log.info("Update event: {}", eventId);
        return ResponseEntity.ok(eventService.updateEvent(eventId, request));
    }

    @DeleteMapping("/{eventId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteEvent(@PathVariable String eventId) {
        log.info("Delete event: {}", eventId);
        return ResponseEntity.ok(eventService.deleteEvent(eventId));
    }
}