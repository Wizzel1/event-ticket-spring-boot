package de.croebe.tickets.controllers;

import de.croebe.tickets.domain.CreateEventRequest;
import de.croebe.tickets.domain.UpdateEventRequest;
import de.croebe.tickets.domain.dtos.*;
import de.croebe.tickets.domain.entities.Event;
import de.croebe.tickets.mappers.EventMapper;
import de.croebe.tickets.services.EventService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping(path = "/api/v1/events")
@RequiredArgsConstructor
public class EventController {

    private final EventMapper eventMapper;
    private final EventService eventService;

    @PostMapping
    public ResponseEntity<CreateEventResponseDto> createEvent(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody CreateEventRequestDto createEventRequestDto
    ) {
        CreateEventRequest request = eventMapper.fromDto(createEventRequestDto);
        UUID userId = this.parseUserId(jwt);
        Event event = eventService.createEvent(userId, request);
        CreateEventResponseDto responseDto = eventMapper.toDto(event);
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    @PutMapping(path = "/{eventId}")
    public ResponseEntity<UpdateEventResponseDto> updateEvent(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable UUID eventId,
            @Valid @RequestBody UpdateEventRequestDto updateEventRequestDto
    ) {
        UpdateEventRequest request = eventMapper.fromDto(updateEventRequestDto);
        UUID userId = this.parseUserId(jwt);
        Event updatedEvent = eventService.updateEventForOrganizer(userId, eventId, request);
        UpdateEventResponseDto responseDto = eventMapper.toUpdateEventResponseDto(updatedEvent);
        return ResponseEntity.ok(responseDto);
    }

    @GetMapping
    public ResponseEntity<Page<ListEventResponseDto>> getEvents(
            @AuthenticationPrincipal Jwt jwt,
            Pageable pageable
    ) {
        UUID userId = this.parseUserId(jwt);
        Page<Event> events = eventService.listEventsForOrganizer(userId, pageable);
        return ResponseEntity.ok(events.map(eventMapper::toListEventResponseDto));
    }

    @GetMapping("/{eventId}")
    public ResponseEntity<GetEventDetailsResponseDto> getEventDetails(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable UUID eventId
    ) {
        UUID userId = this.parseUserId(jwt);
        return eventService
                .getEventForOrganizer(userId, eventId)
                .map(eventMapper::toGetEventDetailsResponseDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    private UUID parseUserId(Jwt jwt) {
        return UUID.fromString(jwt.getSubject());
    }
}
