package com.society.management.service;

import com.society.management.dto.request.EventRequest;
import com.society.management.dto.response.ApiResponse;
import com.society.management.dto.response.EventDto;
import com.society.management.exception.ResourceNotFoundException;
import com.society.management.model.Event;
import com.society.management.repository.EventRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class EventService {

    @Autowired
    private EventRepository eventRepository;

    // CREATE EVENT (ADMIN)
    public ApiResponse<EventDto> createEvent(String adminId, EventRequest request) {
        log.info("Creating event: {}", request.getTitle());

        Event event = Event.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .location(request.getLocation())
                .eventDateTime(request.getEventDateTime())
                .imageUrls(request.getImageUrls())
                .createdByUserId(adminId)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        eventRepository.save(event);
        return ApiResponse.success(mapToDto(event), "Event created successfully");
    }

    // GET ALL EVENTS
    public ApiResponse<Page<EventDto>> getAllEvents(Pageable pageable) {
        log.info("Fetching all events");

        Page<Event> events = eventRepository.findAllByOrderByEventDateTimeAsc(pageable);
        Page<EventDto> eventDtos = events.map(this::mapToDto);

        return ApiResponse.success(eventDtos, "Events fetched successfully");
    }

    // GET UPCOMING EVENTS
    public ApiResponse<List<EventDto>> getUpcomingEvents() {
        log.info("Fetching upcoming events");

        List<Event> events = eventRepository.findUpcomingEvents(LocalDateTime.now());
        List<EventDto> eventDtos = events.stream().map(this::mapToDto).toList();

        return ApiResponse.success(eventDtos, "Upcoming events fetched");
    }

    // GET SINGLE EVENT
    public ApiResponse<EventDto> getEventById(String eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));

        return ApiResponse.success(mapToDto(event), "Event fetched successfully");
    }

    // UPDATE EVENT (ADMIN)
    public ApiResponse<EventDto> updateEvent(String eventId, EventRequest request) {
        log.info("Updating event: {}", eventId);

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));

        event.setTitle(request.getTitle());
        event.setDescription(request.getDescription());
        event.setLocation(request.getLocation());
        event.setEventDateTime(request.getEventDateTime());
        event.setImageUrls(request.getImageUrls());
        event.setUpdatedAt(Instant.now());

        eventRepository.save(event);
        return ApiResponse.success(mapToDto(event), "Event updated successfully");
    }

    // DELETE EVENT (ADMIN)
    public ApiResponse<Void> deleteEvent(String eventId) {
        log.info("Deleting event: {}", eventId);

        if (!eventRepository.existsById(eventId)) {
            throw new ResourceNotFoundException("Event not found");
        }

        eventRepository.deleteById(eventId);
        return ApiResponse.success(null, "Event deleted successfully");
    }

    private EventDto mapToDto(Event event) {
        return EventDto.builder()
                .id(event.getId())
                .title(event.getTitle())
                .description(event.getDescription())
                .location(event.getLocation())
                .eventDateTime(event.getEventDateTime())
                .imageUrls(event.getImageUrls())
                .createdByUserId(event.getCreatedByUserId())
                .createdAt(event.getCreatedAt())
                .updatedAt(event.getUpdatedAt())
                .build();
    }
}